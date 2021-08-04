package cash.super_.platform.service.parkinglot.payment;

import cash.super_.platform.client.parkingplus.model.RetornoConsulta;
import cash.super_.platform.client.payment.PaymentServiceApiClient;
import cash.super_.platform.error.ParkingPlusPaymentNotApprovedException;
import cash.super_.platform.error.supercash.SupercashPaymentErrorException;
import cash.super_.platform.error.supercash.SupercashUnknownHostException;
import cash.super_.platform.service.parkinglot.AbstractParkingLotProxyService;
import cash.super_.platform.service.parkinglot.model.ParkingTicketAuthorizedPaymentStatus;
import cash.super_.platform.service.parkinglot.model.ParkinglotTicket;
import cash.super_.platform.service.parkinglot.model.ParkinglotTicketPayment;
import cash.super_.platform.service.parkinglot.repository.ParkinglotTicketRepository;
import cash.super_.platform.service.parkinglot.repository.PaymentRepository;
import cash.super_.platform.service.parkinglot.ticket.ParkingPlusTicketAuthorizePaymentProxyService;
import cash.super_.platform.service.payment.model.pagarme.*;
import cash.super_.platform.service.payment.model.supercash.*;
import cash.super_.platform.service.payment.model.supercash.amount.Amount;
import cash.super_.platform.service.payment.model.supercash.card.CardRequest;
import cash.super_.platform.service.payment.model.supercash.types.charge.*;
import cash.super_.platform.service.payment.model.supercash.types.order.PaymentOrderResponse;
import cash.super_.platform.utils.IsNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
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
      return paymentStatus;
    }

    LOG.debug("Loading Authorized Payment STATUS for Anonymous Payments: {}", ticketNumber);
    String fieldName;
    ChargePaymentMethodRequest paymentMethodRequest = payRequest.getPaymentMethod();
    paymentMethodRequest.setType(ChargePaymentMethodType.CREDIT_CARD);
    CardRequest card = paymentMethodRequest.getCard();
    fieldName = "Card Number";
    NumberUtil.stringIsLongWithException(card.getNumber(), fieldName);

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

    ParkingTicketAuthorizedPaymentStatus paymentStatus = paymentAuthService.authorizePayment(
            ticketStatus.getNumeroTicket(), ticketPriceWithoutFee, chargeResponse.summary());

    if (!paymentStatus.getStatus().isTicketPago()) {
      throw new SupercashPaymentErrorException("Parking lot service were unable to process payment gateway response. " +
              "Ticket is set to not paid");
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
   * @param ticketStatus
   * @param chargeResponse
   * @param marketplaceId
   * @param storeId
   * @param parkingTicketAuthorizedPaymentStatus
   */
  private void cacheAnonymousAuthorizationPayment(RetornoConsulta ticketStatus, PaymentChargeResponse chargeResponse,
                                                  ParkingTicketAuthorizedPaymentStatus parkingTicketAuthorizedPaymentStatus) {
    Long marketplaceId = supercashRequestContext.getMarketplaceId();
    Long storeId = supercashRequestContext.getStoreId();
    Long userId = supercashRequestContext.getUserId();

    Long paidParkingTicketNumber = Long.parseLong(ticketStatus.getNumeroTicket());
    ParkinglotTicketPayment parkinglotTicketPayment = new ParkinglotTicketPayment();
    parkinglotTicketPayment.setAmount(chargeResponse.getAmount().getSummary().getTotal() - properties.getOurFee());
    parkinglotTicketPayment.setServiceFee(properties.getOurFee());
    parkinglotTicketPayment.setMarketplaceId(Long.valueOf(marketplaceId));
    parkinglotTicketPayment.setStoreId(Long.valueOf(storeId));
    parkinglotTicketPayment.setRequesterService(buildProperties.get("name"));

    Long ticketNumber = Long.valueOf(ticketStatus.getNumeroTicket());
    ParkinglotTicket parkinglotTicket = null;
    Optional<ParkinglotTicket> parkinglotTicketOpt = parkinglotTicketRepository.findByTicketNumberAndUserIdAndStoreId(ticketNumber, userId, storeId);
    if (parkinglotTicketOpt.isPresent()) {
      parkinglotTicket = parkinglotTicketOpt.get();

    } else {
      parkinglotTicket = new ParkinglotTicket();
      parkinglotTicket.setTicketNumber(paidParkingTicketNumber);
      parkinglotTicket.setUserId(Long.valueOf(userId));
      parkinglotTicket.setCreatedAt(ticketStatus.getDataDeEntrada());
    }

    /* Since each payment WPS doesn't store id for each payment, we are going to use the 'dataPagamento' as an id
     * when we need to get specific info about this specific payment.
     * */
    parkinglotTicketPayment.setParkinglotTicket(parkinglotTicket);
    parkinglotTicketPayment.setDate(-1L);
    parkinglotTicket.addPayment(parkinglotTicketPayment);
    parkinglotTicket.addTicketStateTransition(ParkingTicketState.PAID, DateTimeUtil.getNow());
    parkinglotTicket = parkinglotTicketRepository.save(parkinglotTicket);

    // Set the dataPagamento for future use, since this information is returned by the WPS.
    for (ParkinglotTicketPayment payment : parkinglotTicket.getPayments()) {
      if (payment.getDate() == -1) {
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
      LOG.debug("LOADING TESTING TICKET STATUS: {}", ticketNumberToProcess);

      long amountToPay = payRequest.getAmount() + properties.getOurFee();
      ParkingTicketAuthorizedPaymentStatus paymentStatus = testingParkinglotTicketRepository.authorizePayment(
              ticketNumberToProcess, amountToPay);

      LOG.debug("LOADED TESTING TICKET PAYMENT STATUS: {}: {}", ticketNumberToProcess, paymentStatus);
      return paymentStatus;
    }

    String fieldName;
    long saleIdProperty = properties.getSaleId();
    if (saleIdProperty >= 0) {
      payRequest.addMetadata("sale_id", properties.getSaleId().toString());
    }

    fieldName = "CPF or CNPJ";
    NumberUtil.stringIsDoubleWithException(payRequest.getCustomer().getDocuments().get(0).getNumber(), fieldName);

    fieldName = "CEP";
    NumberUtil.stringIsDoubleWithException(payRequest.getBilling().getAddress().getZipcode(), fieldName);

    fieldName = "Card Number";
    NumberUtil.stringIsDoubleWithException(payRequest.getCardNumber(), fieldName);

    fieldName = "Card Name";
    NumberUtil.stringIsDoubleWithException(payRequest.getCardHolderName(), fieldName);

    fieldName = "Card CVV";
    NumberUtil.stringIsDoubleWithException(payRequest.getCardCvv(), fieldName);

    fieldName = "Card Expiration Date";
    NumberUtil.stringIsDoubleWithException(payRequest.getCardExpirationDate(), fieldName);

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
      LOG.debug("PAYMENT AUTHORIZATION RESPONSE: {}", paymentResponse);

    } catch (feign.RetryableException re) {
      LOG.error("PAYMENT AUTHORIZATION FAILED: {}", re);

      if (re.getCause() instanceof UnknownHostException) {
        LOG.error("PAYMENT AUTHORIZATION FAILED with unknown host: {}", re.getCause());
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

    LOG.debug("PAYMENT AUTHORIZATION SUCCEEDED: User IS authorized to make this payment!");

    // Now the verification if the gateway knows whether the user has credit or not
    // The value to show in the parking lot gate screen, to report to the user, etc
    long wpsPaidAmountReported = payRequest.getAmount() - serviceFeeItem.getUnitPrice();

    // Execute the payment for WPS
    LOG.debug("Requesting payment with WPS with the payment request...");
    ParkingTicketAuthorizedPaymentStatus paymentStatus = paymentAuthService.authorizePayment(
            ticketStatus.getNumeroTicket(), wpsPaidAmountReported, paymentResponse.summary());

    if (!paymentStatus.getStatus().isTicketPago()) {
      LOG.error("PAYMENT FAILED: the ticket is not paid after authorization. Check with WPS!");
      throw new SupercashPaymentErrorException(HttpStatus.FORBIDDEN, "Payment not processed by WPS");
    }

    LOG.debug("PAYMENT CAPTURE REQUEST: Requesting payment capture with the Payment Gateway");
    PaymentChargeCaptureRequest paymentChargeCaptureRequest = new PaymentChargeCaptureRequest();
    paymentChargeCaptureRequest.setAmount(chargeResponse.getAmount());
    chargeResponse = paymentServiceApiClient.capturePayment(chargeResponse.getId(), chargeResponse.getPaymentId(),
            paymentChargeCaptureRequest);

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
    cacheParkingTicketPayment(ticketStatus, chargeResponse, serviceFeeItem, marketplaceId, storeId, userId, paymentResponse, paymentStatus);

    return paymentStatus;
  }

  /**
   * Cache (store) the ticket payment in our storage for user's retrieval.
   * @param ticketStatus
   * @param chargeResponse
   * @param serviceFeeItem
   * @param marketplaceId
   * @param storeId
   * @param paymentResponse
   * @param paymentStatus
   */
  private void cacheParkingTicketPayment(RetornoConsulta ticketStatus, PaymentChargeResponse chargeResponse, Item serviceFeeItem,
                                         Long marketplaceId, Long storeId, Long userId, PaymentOrderResponse paymentResponse,
                                         ParkingTicketAuthorizedPaymentStatus paymentStatus) {
    // prepare the ticket payment information
    ParkinglotTicketPayment parkinglotTicketPayment = new ParkinglotTicketPayment();
    parkinglotTicketPayment.setAmount(chargeResponse.getAmount().getSummary().getPaid());
    parkinglotTicketPayment.setServiceFee(serviceFeeItem.getUnitPrice());
    parkinglotTicketPayment.setMarketplaceId(Long.valueOf(marketplaceId));
    parkinglotTicketPayment.setStoreId(Long.valueOf(storeId));
    parkinglotTicketPayment.setRequesterService(buildProperties.get("name"));

    Optional<Payment> paymentOpt = paymentRepository.findById(paymentResponse.getId());
    if (paymentOpt.isPresent()) {
      paymentResponse = (PaymentOrderResponse) paymentOpt.get();
      parkinglotTicketPayment.setPayment(paymentResponse);
    }

    Long ticketNumber = Long.valueOf(ticketStatus.getNumeroTicket());
    Optional<ParkinglotTicket> parkinglotTicketOpt = parkinglotTicketRepository.findByTicketNumberAndUserIdAndStoreId(ticketNumber, userId, storeId);

    ParkinglotTicket parkinglotTicket = null;
    if (parkinglotTicketOpt.isPresent()) {
      parkinglotTicket = parkinglotTicketOpt.get();

    } else {
      parkinglotTicket = new ParkinglotTicket();
      parkinglotTicket.setTicketNumber(Long.valueOf(ticketNumber));
      parkinglotTicket.setUserId(Long.valueOf(userId));
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

    // Set the dataPagamento for future use, since this information is returned by the WPS.
    for (ParkinglotTicketPayment payment : parkinglotTicket.getPayments()) {
      if (payment.getDate() == -1) {
        payment.setDate(paymentStatus.getStatus().getDataPagamento());
        break;
      }
    }

    // Store in the repository
    parkinglotTicketRepository.save(parkinglotTicket);
  }
}