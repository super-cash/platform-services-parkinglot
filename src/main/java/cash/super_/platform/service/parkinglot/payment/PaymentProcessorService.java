package cash.super_.platform.service.parkinglot.payment;

import cash.super_.platform.client.parkingplus.model.RetornoConsulta;
import cash.super_.platform.client.parkingplus.model.RetornoPagamento;
import cash.super_.platform.client.payment.PaymentServiceApiClient;
import cash.super_.platform.client.wps.error.ParkingPlusPaymentNotApprovedException;
import cash.super_.platform.error.parkinglot.SupercashInvalidValueException;
import cash.super_.platform.error.parkinglot.SupercashPaymentErrorException;
import cash.super_.platform.adapter.feign.SupercashUnknownHostException;
import cash.super_.platform.model.supercash.Payment;
import cash.super_.platform.model.supercash.PaymentChargeCaptureRequest;
import cash.super_.platform.model.payment.pagarme.Item;
import cash.super_.platform.model.payment.pagarme.SplitRule;
import cash.super_.platform.model.payment.pagarme.Transaction;
import cash.super_.platform.model.payment.pagarme.TransactionRequest;
import cash.super_.platform.model.supercash.card.CardResponse;
import cash.super_.platform.repository.ParkinglotTicketPaymentsRepository;
import cash.super_.platform.service.parkinglot.AbstractParkingLotProxyService;
import cash.super_.platform.model.parkinglot.ParkingTicketAuthorizedPaymentStatus;
import cash.super_.platform.model.parkinglot.ParkingTicketState;
import cash.super_.platform.model.parkinglot.ParkinglotTicket;
import cash.super_.platform.model.parkinglot.ParkinglotTicketPayment;
import cash.super_.platform.repository.ParkinglotTicketRepository;
import cash.super_.platform.repository.PaymentRepository;
import cash.super_.platform.service.parkinglot.ticket.parkingplus.ParkingPlusTicketAuthorizePaymentProxyService;
import cash.super_.platform.model.supercash.amount.Amount;
import cash.super_.platform.model.supercash.card.CardRequest;
import cash.super_.platform.model.supercash.types.charge.*;
import cash.super_.platform.model.supercash.types.order.PaymentOrderResponse;
import cash.super_.platform.util.DateTimeUtil;
import cash.super_.platform.util.FieldType;
import cash.super_.platform.util.NumberUtil;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Proxy service to Retrieve the status of tickets, process payments, etc.
 * 
 * https://demonstracao.parkingplus.com.br/servicos/swagger-ui.html#!/servico-pagamento-ticket-2/pagarTicketAutorizadoUsingPOST
 *
 * @author leandro
 *
 */
@Service
public class PaymentProcessorService extends AbstractParkingLotProxyService {

  protected static final Logger LOG = LoggerFactory.getLogger(PaymentProcessorService.class);

  @Autowired
  private PaymentServiceApiClient paymentServiceApiClient;

  @Autowired
  private BuildProperties buildProperties;

  @Autowired
  private ParkingPlusTicketAuthorizePaymentProxyService paymentAuthService;

  @Autowired
  private ParkinglotTicketRepository parkinglotTicketRepository;

  @Autowired
  private ParkinglotTicketPaymentsRepository parkinglotTicketPaymentsRepository;

  @Autowired
  private PaymentRepository paymentRepository;

  // TODO: Refactor this method (processPayment)
  public ParkingTicketAuthorizedPaymentStatus processPayment(AnonymousPaymentChargeRequest payRequest, RetornoConsulta ticketStatus) {
    // load the ticket status or load a testing ticket
    final String ticketNumber = ticketStatus.getNumeroTicket();
    if (testingParkinglotTicketRepository.containsTicket(ticketNumber)) {
      LOG.debug("LOADING TESTING Ticket Authorized Payment STATUS for Anonymous Payments: {}", ticketNumber);

      long amountToPay = payRequest.getAmount().getValue() + properties.getOurFee();
      ParkingTicketAuthorizedPaymentStatus paymentStatus = testingParkinglotTicketRepository.authorizePayment(
              ticketNumber, amountToPay);

      LOG.debug("LOADED TESTING Authorized Payment STATUS for Anonymous Payments: {}: {}", ticketNumber, paymentStatus);
      testingParkinglotTicketRepository.updateStatus(ticketNumber, ParkingTicketState.PAID);
      return paymentStatus;
    }

    LOG.debug("Loading Authorized Payment STATUS for Anonymous Payments: {}", ticketNumber);
    String fieldName;
    ChargePaymentMethodRequest paymentMethodRequest = payRequest.getPaymentMethod();
    paymentMethodRequest.setType(ChargePaymentMethodType.CREDIT_CARD);
    CardRequest card = paymentMethodRequest.getCard();
    fieldName = "Card Number";
    NumberUtil.stringIsLongWithException(FieldType.VALUE, card.getNumber(), fieldName);

    if (Strings.isNullOrEmpty(card.getHolder().getName())) {
      throw new SupercashInvalidValueException("Cart holder name must be provided");
    }

    List<SplitRule> splitRules = new ArrayList<>();
    SplitRule ourClient = new SplitRule();
    ourClient.setRecipientId(properties.getClientRecipientId());
    ourClient.setLiable(true);
    ourClient.setChargeRemainderFee(true);
    ourClient.setChargeProcessingFee(false);
    SplitRule us = new SplitRule();
    us.setRecipientId(properties.getOurRecipientId());
    us.setLiable(true);
    us.setChargeRemainderFee(true);
    us.setChargeProcessingFee(true);

    // TODO: Split rule must be used for pagseguro, but it is not supported by them, we have to deal with this in our
    //  side.
    /* Calculating our client amount to receive, based on percentage negociated. */
    Double ourClientAmount = payRequest.getAmount().getValue() * properties.getClientPercentage() / 100;
    ourClient.setAmount(ourClientAmount.longValue());
    us.setAmount(payRequest.getAmount().getValue() - ourClient.getAmount() + properties.getOurFee());
    splitRules.add(ourClient);
    splitRules.add(us);

    Long ticketPriceWithoutFee = payRequest.getAmount().getValue();
    payRequest.setAmount(new Amount(payRequest.getAmount().getValue() + properties.getOurFee()));

    paymentMethodRequest.setInstallments(1);
    paymentMethodRequest.setCapture(false);

    // Verify if the ticket is new and just got scanned, and if so, it has 3 initial states
    Long marketplaceId = supercashRequestContext.getMarketplaceId();
    Long storeId = supercashRequestContext.getStoreId();
    Long userId = supercashRequestContext.getUserId();

    payRequest.addMetadata("ticket_number", ticketStatus.getNumeroTicket());
    payRequest.addMetadata("service_fee", properties.getServiceFeeItemTitle());
    payRequest.addMetadata("marketplace_id", String.valueOf(marketplaceId));
    payRequest.addMetadata("store_id", String.valueOf(storeId));
    payRequest.addMetadata("user_id", String.valueOf(userId));
    payRequest.addMetadata("requester_service", buildProperties.get("name"));

    Long allowedExitDateTime = ticketStatus.getDataPermitidaSaidaUltimoPagamento();
    if (allowedExitDateTime == null) {
      allowedExitDateTime = ticketStatus.getDataPermitidaSaida();
    }
    payRequest.addMetadata("lapsed_time", String.valueOf(allowedExitDateTime - ticketStatus.getDataDeEntrada()));

    LOG.debug("Requesting Payment Authorization for Anonymous Payments: {}", ticketNumber);
    // Authorize the payment first
    PaymentChargeResponse chargeResponse = null;
    try {
      chargeResponse = paymentServiceApiClient.authorizePayment(payRequest);
      LOG.debug("Payment Authorization for Anonymous Payments SUCCESS: {}", chargeResponse);

    } catch (feign.RetryableException re) {
      LOG.error("Payment Authorization for Anonymous Payments FAILED: {}", chargeResponse);

      if (re.getCause() instanceof UnknownHostException) {
        LOG.error("Payment Authorization for Anonymous Payments FAILED unknown host: {}", re.getCause().getMessage());
        throw new SupercashUnknownHostException("Host '" + re.getCause().getMessage() + "' unknown.");
      }
      throw re;
    }

    if (chargeResponse.getStatus() != ChargeStatus.AUTHORIZED) {
      LOG.error("Payment Authorization for Anonymous Payments FAILED: with status {}", chargeResponse.getStatus());
      throw new SupercashPaymentErrorException(HttpStatus.FORBIDDEN, "Payment method not authorized.");
    }

    ParkingTicketAuthorizedPaymentStatus paymentStatus;
    Map<String, String> metadata = payRequest.getMetadata();
    if (metadata.containsKey("testing") && metadata.get("testing") != null && metadata.get("testing").equals("true")) {
      paymentStatus = new ParkingTicketAuthorizedPaymentStatus();
      long now = DateTimeUtil.getNow();
      RetornoPagamento retornoPagamento = new RetornoPagamento()
              .dataHoraSaida(now)
              .dataPagamento(now)
              .errorCode(0)
              .mensagem("Pagamento efetuado com sucesso.")
              .numeroTicket(ticketNumber)
              .ticketPago(true);
      paymentStatus.setStatus(retornoPagamento);

    } else {
      paymentStatus = paymentAuthService.authorizePayment(ticketStatus.getNumeroTicket(), ticketPriceWithoutFee,
              chargeResponse.summary());
      if (!paymentStatus.getStatus().isTicketPago()) {
        throw new SupercashPaymentErrorException("Parking lot service were unable to process payment gateway response. " +
                "Ticket is set to not paid");
      }
    }

    LOG.debug("Payment Capture request for Anonymous Payments: amount={}", chargeResponse.getAmount());
    PaymentChargeCaptureRequest paymentChargeCaptureRequest = new PaymentChargeCaptureRequest();
    paymentChargeCaptureRequest.setAmount(chargeResponse.getAmount());
    chargeResponse = paymentServiceApiClient.capturePayment(chargeResponse.getId(), chargeResponse.getPaymentId(),
            paymentChargeCaptureRequest);

    if (chargeResponse.getStatus() != ChargeStatus.PAID) {
      // TODO: Review this situation, since the ticket is authorized, but the payment was not effectivelly
      //  possible, although AUTHORIZED. The best decision is to send a WPS request to rollback the request of
      //  payment authorization.
      ParkingPlusPaymentNotApprovedException exception =
              new ParkingPlusPaymentNotApprovedException(HttpStatus.FORBIDDEN, "Payment status is " +
                      chargeResponse.getStatus());
      LOG.error("Payment Capture request for Anonymous Payments failed with status different than paid: status={}: {}",
              chargeResponse.getAmount(), exception.getMessage());
      // TODO: Send a report to us (via email or sms) to report this situation.
      throw exception;
    }

    LOG.debug("Anonymous payment captured successfully and will be stored: {}", chargeResponse);
    // Cache the payment in our storage so the user can view them
    cacheAnonymousAuthorizationPayment(ticketStatus, chargeResponse, paymentStatus);
    return paymentStatus;
  }

  /**
   * Store the payment in our database
   * @param ticketStatus the ticket status from WPS
   * @param chargeResponse from our payment system
   * @param parkingTicketAuthorizedPaymentStatus the authorization status
   */
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
  void cacheAnonymousAuthorizationPayment(RetornoConsulta ticketStatus, PaymentChargeResponse chargeResponse,
                                                  ParkingTicketAuthorizedPaymentStatus parkingTicketAuthorizedPaymentStatus) {
    Long storeId = supercashRequestContext.getStoreId();
    Long userId = supercashRequestContext.getUserId();

    Long ticketNumber = Long.parseLong(ticketStatus.getNumeroTicket());
    ParkinglotTicketPayment parkinglotTicketPayment = new ParkinglotTicketPayment();
    parkinglotTicketPayment.setAmount(chargeResponse.getAmount().getSummary().getTotal() - properties.getOurFee());
    parkinglotTicketPayment.setServiceFee(properties.getOurFee());
    parkinglotTicketPayment.setUserId(Long.valueOf(userId));
    parkinglotTicketPayment.setRequesterService(buildProperties.get("name"));

    Optional<Payment> paymentOpt = paymentRepository.findById(chargeResponse.getId());
    if (paymentOpt.isPresent()) {
      parkinglotTicketPayment.setPayment((PaymentChargeResponse) paymentOpt.get());
      LOG.debug("Payment order ID is present for the ticketId={}: {}", ticketNumber, chargeResponse);
    }

    Optional<ParkinglotTicket> parkinglotTicketOpt = parkinglotTicketRepository.findByTicketNumberAndStoreId(ticketNumber, storeId);
    ParkinglotTicket parkinglotTicket = null;
    if (parkinglotTicketOpt.isPresent()) {
      parkinglotTicket = parkinglotTicketOpt.get();
      LOG.debug("Retrieved current parkinglot ticket to add payment={}: {}", ticketNumber, parkinglotTicket);

    } else {
      LOG.debug("Creating new parkinglot ticket for first payment={}: {}", ticketNumber, parkinglotTicket);
      parkinglotTicket = new ParkinglotTicket();
      parkinglotTicket.setTicketNumber(Long.valueOf(ticketNumber));
      parkinglotTicket.setStoreId(storeId);
      parkinglotTicket.setCreatedAt(ticketStatus.getDataDeEntrada());
    }

    /*
     * Since each payment WPS doesn't store id for each payment, we are going to use the 'dataPagamento' as an id
     * when we need to get specific info about this specific payment.
     */
    parkinglotTicketPayment.setParkinglotTicket(parkinglotTicket);
    parkinglotTicketPayment.setDate(-1L);
    parkinglotTicket.addPayment(parkinglotTicketPayment);

    // Save the parkinglot ticket
//    parkinglotTicket.addTicketStateTransition(ParkingTicketState.PAID, userId, DateTimeUtil.getNow());
    parkinglotTicket = parkinglotTicketRepository.save(parkinglotTicket);
    parkinglotTicketPayment.setParkinglotTicket(parkinglotTicket);
    parkinglotTicketPaymentsRepository.save(parkinglotTicketPayment);

    // Set the dataPagamento for future use, since this information is returned by the WPS.
    for (ParkinglotTicketPayment payment : parkinglotTicket.getPayments()) {
      if (payment.getDate() == null || payment.getDate() == -1) {
        payment.setDate(parkingTicketAuthorizedPaymentStatus.getStatus().getDataPagamento());
        break;
      }
    }

    // Store in the repository
    parkinglotTicketRepository.save(parkinglotTicket);
  }

  // TODO: Refactor this method (processPayment)
  public ParkingTicketAuthorizedPaymentStatus processPayment(TransactionRequest payRequest, RetornoConsulta ticketStatus) {
    // load the ticket status or load a testing ticket
    final String ticketNumberToProcess = ticketStatus.getNumeroTicket();
    if (testingParkinglotTicketRepository.containsTicket(ticketNumberToProcess)) {
      LOG.debug("Loading status for testing ticket: {}", ticketNumberToProcess);

      long amountToPay = payRequest.getAmount() + properties.getOurFee();
      ParkingTicketAuthorizedPaymentStatus paymentStatus = testingParkinglotTicketRepository.authorizePayment(
              ticketNumberToProcess, amountToPay);

      LOG.debug("Loading payments for testing ticket={}: {}", ticketNumberToProcess, paymentStatus);
      return paymentStatus;
    }

    String fieldName;
    long saleIdProperty = properties.getSaleId();
    if (saleIdProperty >= 0) {
      payRequest.addMetadata("sale_id", properties.getSaleId().toString());
    }

    fieldName = "CPF or CNPJ";
    NumberUtil.stringIsLongWithException(FieldType.VALUE, payRequest.getCustomer().getDocuments().get(0).getNumber(), fieldName);

    fieldName = "CEP";
    NumberUtil.stringIsLongWithException(FieldType.VALUE, payRequest.getBilling().getAddress().getZipcode(), fieldName);

    fieldName = "Card Number";
    NumberUtil.stringIsLongWithException(FieldType.VALUE, payRequest.getCardNumber(), fieldName);

    fieldName = "Card CVV";
    NumberUtil.stringIsLongWithException(FieldType.VALUE, payRequest.getCardCvv(), fieldName);

    fieldName = "Card Expiration Date";
    NumberUtil.stringIsLongWithException(FieldType.VALUE, payRequest.getCardExpirationDate(), fieldName);

    List<SplitRule> splitRules = new ArrayList<>();
    SplitRule ourClient = new SplitRule();
    ourClient.setRecipientId(properties.getClientRecipientId());
    ourClient.setLiable(true);
    ourClient.setChargeRemainderFee(true);
    ourClient.setChargeProcessingFee(false);
    SplitRule us = new SplitRule();
    us.setRecipientId(properties.getOurRecipientId());
    us.setLiable(true);
    us.setChargeRemainderFee(true);
    us.setChargeProcessingFee(true);

    Long total = payRequest.getAmount();

    /* Calculating our client amount to receive, based on percentage negociated. */
    Double ourClientAmount = total * properties.getClientPercentage() / 100;
    ourClient.setAmount(ourClientAmount.longValue());

    us.setAmount(total - ourClient.getAmount() + properties.getOurFee());

    splitRules.add(ourClient);
    splitRules.add(us);

    Item ticketItem = new Item();
    ticketItem.setId(ticketStatus.getNumeroTicket());
    ticketItem.setUnitPrice(payRequest.getAmount());
    ticketItem.setQuantity(1);
    ticketItem.setTangible(false);
    ticketItem.setTitle(properties.getTicketItemTitle());
    payRequest.addItem(ticketItem);

    Item serviceFeeItem = new Item();
    serviceFeeItem.setId("1");
    serviceFeeItem.setQuantity(1);
    serviceFeeItem.setTangible(false);
    serviceFeeItem.setTitle(properties.getServiceFeeItemTitle());
    serviceFeeItem.setUnitPrice(properties.getOurFee());
    payRequest.addItem(serviceFeeItem);
    payRequest.setAmount(payRequest.getAmount() + properties.getOurFee());
    payRequest.setSplitRules(splitRules);
    payRequest.setPaymentMethod(Transaction.PaymentMethod.CREDIT_CARD);

    Long marketplaceId = supercashRequestContext.getMarketplaceId();
    Long storeId = supercashRequestContext.getStoreId();
    Long userId = supercashRequestContext.getUserId();

    payRequest.addMetadata("ticket_number", ticketItem.getId());
    payRequest.addMetadata("service_fee", serviceFeeItem.getUnitPrice().toString());
    payRequest.addMetadata("marketplace_id", String.valueOf(marketplaceId));
    payRequest.addMetadata("store_id", String.valueOf(storeId));
    payRequest.addMetadata("user_id", String.valueOf(userId));
    payRequest.addMetadata("requester_service", buildProperties.get("name"));

    // validate by the exit date
    Long allowedExitDateTime = ticketStatus.getDataPermitidaSaidaUltimoPagamento();
    if (allowedExitDateTime == null) {
      allowedExitDateTime = ticketStatus.getDataPermitidaSaida();
    }
    payRequest.addMetadata("lapsed_time", String.valueOf(allowedExitDateTime - ticketStatus.getDataDeEntrada()));
    payRequest.setCapture(false);
    payRequest.setAsync(false);

    // Send message to the gateway to verify if "pagseguro" knows whether the user has credit or not
    PaymentOrderResponse paymentResponse = null;
    try {
      paymentResponse = paymentServiceApiClient.authorizePayment(payRequest.toSupercashPaymentOrderRequest());
      LOG.debug("Payment authorization response: {}", paymentResponse);

    } catch (feign.RetryableException re) {
      LOG.error("Payment authorization failed for ticket={}: {}", ticketNumberToProcess, re);

      if (re.getCause() instanceof UnknownHostException) {
        LOG.error("Payment authorization failed due unreachable host for ticket={}: {}", ticketNumberToProcess, re.getCause());
        throw new SupercashUnknownHostException("Host '" + re.getCause().getMessage() + "' unknown.");
      }
      throw re;
    }

    // Any error occurred with the response from the payment system
    if (paymentResponse == null || paymentResponse.getId() == null) {
      LOG.error("PAYMENT AUTHORIZATION FAILED response is null. Check payment service!");
      throw new SupercashPaymentErrorException("Parking lot service were unable to process payment gateway response. " +
              "The response id is null.");
    }

    PaymentChargeResponse chargeResponse = paymentResponse.getCharges().stream().findFirst().get();
    LOG.debug("PAYMENT AUTHORIZATION SUCEEDED with response: {}", chargeResponse);

    if (chargeResponse.getStatus() != ChargeStatus.AUTHORIZED) {
      LOG.error("PAYMENT AUTHORIZATION FAILED: User is not authorized to make this payment!");
      throw new SupercashPaymentErrorException(HttpStatus.FORBIDDEN, "Payment method not authorized.");
    }

    LOG.debug("PAYMENT AUTHORIZATION SUCCEEDED: User IS authorized to make this payment for ticket={}!", ticketNumberToProcess);

    // Now the verification if the gateway knows whether the user has credit or not
    // The value to show in the parking lot gate screen, to report to the user, etc
    long wpsPaidAmountReported = payRequest.getAmount() - serviceFeeItem.getUnitPrice();

    // Execute the payment for WPS
    LOG.debug("Requesting payment with WPS with the payment request...");

    ParkingTicketAuthorizedPaymentStatus paymentStatus;
    Map<String, String> metadata = payRequest.getMetadata();
    if (metadata.containsKey("testing") && metadata.get("testing") != null && metadata.get("testing").equals("true")) {
      paymentStatus = new ParkingTicketAuthorizedPaymentStatus();
      long now = DateTimeUtil.getNow();
      RetornoPagamento retornoPagamento = new RetornoPagamento()
              .dataHoraSaida(now)
              .dataPagamento(now)
              .errorCode(0)
              .mensagem("Pagamento efetuado com sucesso.")
              .numeroTicket(ticketStatus.getNumeroTicket())
              .ticketPago(true);
      paymentStatus.setStatus(retornoPagamento);

    } else {
      paymentStatus = paymentAuthService.authorizePayment(ticketStatus.getNumeroTicket(), wpsPaidAmountReported,
              paymentResponse.summary());

      if (!paymentStatus.getStatus().isTicketPago()) {
        LOG.error("PAYMENT FAILED: the ticket is not paid after authorization. Check with WPS!");
        throw new SupercashPaymentErrorException(HttpStatus.FORBIDDEN, "Payment not processed by WPS");
      }
    }

    LOG.debug("PAYMENT CAPTURE REQUEST: Requesting payment capture with the Payment Gateway");
    PaymentChargeCaptureRequest paymentChargeCaptureRequest = new PaymentChargeCaptureRequest();
    paymentChargeCaptureRequest.setAmount(chargeResponse.getAmount());
    chargeResponse = paymentServiceApiClient.capturePayment(chargeResponse.getId(), chargeResponse.getPaymentId(),
            paymentChargeCaptureRequest);
    LOG.debug("PAYMENT CAPTURE RESPONSE: payment response: {}", chargeResponse);

    // TODO: Review this situation, since the ticket is authorized, but the payment was not effectivelly
    //  possible, although AUTHORIZED. The best decision is to send a WPS request to rollback the request of
    //  payment authorization.
    if (chargeResponse.getStatus() != ChargeStatus.PAID) {
      ParkingPlusPaymentNotApprovedException exception = new ParkingPlusPaymentNotApprovedException(
              HttpStatus.FORBIDDEN, "Payment status is " + chargeResponse.getStatus());
      LOG.error("The payment status did not change to PAID: {}", exception.getMessage());
      throw exception;
    }

    // Save the value in our storage for the user's status
    cacheParkingTicketPayment(ticketStatus, serviceFeeItem, storeId, userId, paymentResponse, paymentStatus);

    return paymentStatus;
  }

  /**
   * Cache (store) the ticket payment in our storage for user's retrieval.
   * @param ticketStatus
   * @param serviceFeeItem
   * @param storeId
   * @param paymentStatus
   */
  private void cacheParkingTicketPayment(RetornoConsulta ticketStatus, Item serviceFeeItem, Long storeId, Long userId,
                                         PaymentOrderResponse paymentOrderResponse,
                                         ParkingTicketAuthorizedPaymentStatus paymentStatus) {

    Long ticketNumber = Long.valueOf(ticketStatus.getNumeroTicket());
    LOG.debug("Caching the parking payment order for parking ticketId={}: {}", ticketNumber, paymentOrderResponse);

    // prepare the ticket payment information
    ParkinglotTicketPayment parkinglotTicketPayment = new ParkinglotTicketPayment();
    parkinglotTicketPayment.setAmount(paymentOrderResponse.getCharges().stream().findFirst().get().getAmount().getValue());
    parkinglotTicketPayment.setServiceFee(serviceFeeItem.getUnitPrice());
    parkinglotTicketPayment.setUserId(Long.valueOf(userId));
    parkinglotTicketPayment.setRequesterService(buildProperties.get("name"));

    Optional<Payment> paymentOpt = paymentRepository.findById(paymentOrderResponse.getId());
    if (paymentOpt.isPresent()) {
      paymentOrderResponse = (PaymentOrderResponse) paymentOpt.get();
      parkinglotTicketPayment.setPayment(paymentOrderResponse.getCharges().stream().findFirst().get());
        LOG.debug("Payment order ID is present for the ticketId={}: {}", ticketNumber, paymentOrderResponse);
    }

    Optional<ParkinglotTicket> parkinglotTicketOpt = parkinglotTicketRepository.findByTicketNumberAndStoreId(ticketNumber, storeId);
    ParkinglotTicket parkinglotTicket = null;
    if (parkinglotTicketOpt.isPresent()) {
      parkinglotTicket = parkinglotTicketOpt.get();
      LOG.debug("Retrieved current parkinglot ticket to add payment={}: {}", ticketNumber, parkinglotTicket);

    } else {
      LOG.debug("Creating new parkinglot ticket for first payment={}: {}", ticketNumber, parkinglotTicket);
      parkinglotTicket = new ParkinglotTicket();
      parkinglotTicket.setTicketNumber(Long.valueOf(ticketNumber));
      parkinglotTicket.setStoreId(storeId);
      parkinglotTicket.setCreatedAt(ticketStatus.getDataDeEntrada());
    }

    /*
     * Since each payment WPS doesn't store id for each payment, we are going to use the 'dataPagamento' as an id
     * when we need to get specific info about this specific payment.
     */
    parkinglotTicketPayment.setParkinglotTicket(parkinglotTicket);
    parkinglotTicketPayment.setDate(-1L);
    parkinglotTicket.addPayment(parkinglotTicketPayment);

    // Save the parkinglot ticket
    parkinglotTicket = parkinglotTicketRepository.save(parkinglotTicket);
    parkinglotTicketPayment.setParkinglotTicket(parkinglotTicket);
    parkinglotTicketPaymentsRepository.save(parkinglotTicketPayment);

    // Set the dataPagamento for future use, since this information is returned by the WPS.
    for (ParkinglotTicketPayment payment : parkinglotTicket.getPayments()) {
      if (payment.getDate() == null || payment.getDate() == -1) {
        payment.setDate(paymentStatus.getStatus().getDataPagamento());
        LOG.debug("Set payment date date for ticket={}: {}", ticketNumber, paymentStatus.getStatus().getDataPagamento());
        break;
      }
    }

    // Store in the repository
    LOG.debug("Attempt to save the ticket payment for history={}: {}", ticketNumber, parkinglotTicket);
    parkinglotTicketRepository.save(parkinglotTicket);
    LOG.debug("Set payment date date for ticket={}: {}", ticketNumber, paymentStatus.getStatus().getDataPagamento());
  }
}