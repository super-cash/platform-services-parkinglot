package cash.super_.platform.service.parkinglot.payment;

import cash.super_.platform.client.parkingplus.model.RetornoConsulta;
import cash.super_.platform.clients.payment.PaymentServiceApiClient;
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
  public ParkingTicketAuthorizedPaymentStatus processPayment(AnonymousPaymentChargeRequest payRequest, RetornoConsulta ticketStatus,
                                                             String userId, String marketplaceId, String storeId) {
    String fieldName;

    ChargePaymentMethodRequest paymentMethodRequest = payRequest.getPaymentMethod();
    paymentMethodRequest.setType(ChargePaymentMethodType.CREDIT_CARD);
    CardRequest card = paymentMethodRequest.getCard();
    fieldName = "Card Number";
    IsNumber.stringIsDoubleWithException(card.getNumber(), fieldName);

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
//    payRequest.setSplitRules(splitRules);

    Long ticketPriceWithoutFee = payRequest.getAmount().getValue();
    payRequest.setAmount(new Amount(payRequest.getAmount().getValue() + properties.getOurFee()));

    paymentMethodRequest.setInstallments(1);
    paymentMethodRequest.setCapture(false);

    payRequest.addMetadata("ticket_number", ticketStatus.getNumeroTicket());
    payRequest.addMetadata("service_fee", properties.getServiceFeeItemTitle());
    payRequest.addMetadata("marketplace_id", marketplaceId);
    payRequest.addMetadata("store_id", storeId);
    payRequest.addMetadata("user_id", userId);
    payRequest.addMetadata("requester_service", buildProperties.get("name"));
    Long allowedExitDateTime = ticketStatus.getDataPermitidaSaidaUltimoPagamento();
    if (allowedExitDateTime == null) {
      allowedExitDateTime = ticketStatus.getDataPermitidaSaida();
    }
    payRequest.addMetadata("lapsed_time", String.valueOf(allowedExitDateTime - ticketStatus.getDataDeEntrada()));

    PaymentChargeResponse chargeResponse = null;
    try {
      chargeResponse = paymentServiceApiClient.authorizePayment(payRequest);
      LOG.error("PAYMENT RESPONSE: {}", chargeResponse);
    } catch (feign.RetryableException re) {
      if (re.getCause() instanceof UnknownHostException) {
        throw new SupercashUnknownHostException("Host '" + re.getCause().getMessage() + "' unknown.");
      }
      throw re;
    }

    ParkingTicketAuthorizedPaymentStatus parkingTicketAuthorizedPaymentStatus = null;
    if (chargeResponse.getStatus() == ChargeStatus.AUTHORIZED) {
      userId =  properties.getUdidPrefix() + "-" + marketplaceId + "-" + storeId + "-" + userId;
      parkingTicketAuthorizedPaymentStatus = paymentAuthService.authorizePayment(userId,
              ticketStatus.getNumeroTicket(), ticketPriceWithoutFee, chargeResponse.summary());
      if (parkingTicketAuthorizedPaymentStatus.getStatus().isTicketPago()) {
        PaymentChargeCaptureRequest paymentChargeCaptureRequest = new PaymentChargeCaptureRequest();
        paymentChargeCaptureRequest.setAmount(chargeResponse.getAmount());
        chargeResponse = paymentServiceApiClient.capturePayment(chargeResponse.getId(), chargeResponse.getPaymentId(),
                paymentChargeCaptureRequest);
        if (chargeResponse.getStatus() == ChargeStatus.PAID) {
          /* Saving payment request into the database */
          Long ticketNumber = Long.parseLong(ticketStatus.getNumeroTicket());
          ParkinglotTicketPayment parkinglotTicketPayment = new ParkinglotTicketPayment();
          parkinglotTicketPayment.setAmount(chargeResponse.getAmount().getSummary().getPaid());
          parkinglotTicketPayment.setServiceFee(properties.getOurFee());
          parkinglotTicketPayment.setMarketplaceId(Long.valueOf(marketplaceId));
          parkinglotTicketPayment.setStoreId(Long.valueOf(storeId));
          parkinglotTicketPayment.setRequesterService(buildProperties.get("name"));

          // TODO: implement charge storage
//          Optional<Payment> paymentOpt = paymentRepository.findById(chargeResponse.getId());
//          if (paymentOpt.isPresent()) {
//            chargeResponse = (PaymentChargeResponse) paymentOpt.get();
//            parkinglotTicketPayment.setPayment(chargeResponse);
//          }
//      parkinglotTicketPayment.setPayment(paymentResponse);

          ParkinglotTicket parkinglotTicket = null;
          Optional<ParkinglotTicket> parkinglotTicketOpt = parkinglotTicketRepository.findByTicketNumber(ticketNumber);
          if (parkinglotTicketOpt.isPresent()) {
            parkinglotTicket = parkinglotTicketOpt.get();
          } else {
            parkinglotTicket = new ParkinglotTicket();
            parkinglotTicket.setTicketNumber(ticketNumber);
          }

          /* Since each payment WPS doesn't store id for each payment, we are going to use the 'dataPagamento' as an id
           * when we need to get specific info about this specific payment.
           * */
          parkinglotTicketPayment.setParkinglotTicket(parkinglotTicket);
          parkinglotTicketPayment.setDate(-1L);
          parkinglotTicket.addPayment(parkinglotTicketPayment);
          parkinglotTicket = parkinglotTicketRepository.save(parkinglotTicket);

          for (int i = 0; i < parkinglotTicket.getPayments().size(); i++) {
            parkinglotTicketPayment = parkinglotTicket.getPayments().get(i);
            if (parkinglotTicketPayment.getDate() == -1) {
              /* Set the dataPagamento for future use, since this information is returned by the WPS. */
              parkinglotTicketPayment.setDate(parkingTicketAuthorizedPaymentStatus.getStatus().getDataPagamento());
              break;
            }
          }
          parkinglotTicketRepository.save(parkinglotTicket);
          return parkingTicketAuthorizedPaymentStatus;
        } else {
          // TODO: Review this situation, since the ticket is authorized, but the payment was not effectivelly
          //  possible, although AUTHORIZED. The best decision is to send a WPS request to rollback the request of
          //  payment authorization.
          ParkingPlusPaymentNotApprovedException exception =
                  new ParkingPlusPaymentNotApprovedException(HttpStatus.FORBIDDEN, "Payment status is " +
                          chargeResponse.getStatus());
          LOG.error(exception.getMessage());
          // TODO: Send a report to us (via email or sms) to report this situation.
          throw exception;
        }
      }
    } else {
      throw new SupercashPaymentErrorException(HttpStatus.FORBIDDEN, "Payment method not authorized.");
    }
    throw new SupercashPaymentErrorException("Parking lot service were unable to process payment gateway response. " +
            "The response id is null.");

  }

  // TODO: Refactor this method (processPayment)
  public ParkingTicketAuthorizedPaymentStatus processPayment(TransactionRequest payRequest, RetornoConsulta ticketStatus,
                                                             String userId, String marketplaceId, String storeId) {
    String fieldName;
    long saleIdProperty = properties.getSaleId();
    if (saleIdProperty >= 0) {
      payRequest.addMetadata("sale_id", properties.getSaleId().toString());
    }

    fieldName = "CPF or CNPJ";
    IsNumber.stringIsDoubleWithException(payRequest.getCustomer().getDocuments().get(0).getNumber(), fieldName);

    fieldName = "CEP";
    IsNumber.stringIsDoubleWithException(payRequest.getBilling().getAddress().getZipcode(), fieldName);

    fieldName = "Card Number";
    IsNumber.stringIsDoubleWithException(payRequest.getCardNumber(), fieldName);

    fieldName = "Card CVV";
    IsNumber.stringIsDoubleWithException(payRequest.getCardCvv(), fieldName);

    fieldName = "Card Expiration Date";
    IsNumber.stringIsDoubleWithException(payRequest.getCardExpirationDate(), fieldName);

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

    payRequest.addMetadata("ticket_number", ticketItem.getId());
    payRequest.addMetadata("service_fee", serviceFeeItem.getUnitPrice().toString());
    payRequest.addMetadata("marketplace_id", marketplaceId);
    payRequest.addMetadata("store_id", storeId);
    payRequest.addMetadata("user_id", userId);
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
    userId =  properties.getUdidPrefix() + "-" + marketplaceId + "-" + storeId + "-" + userId;

    // Now the verification if the gateway knows whether the user has credit or not
    // The value to show in the parking lot gate screen, to report to the user, etc
    long wpsPaidAmountReported = payRequest.getAmount() - serviceFeeItem.getUnitPrice();

    // Execute the payment for WPS
    LOG.debug("Requesting payment with WPS with the payment request...");
    ParkingTicketAuthorizedPaymentStatus paymentStatus = paymentAuthService.authorizePayment(userId,
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
    cacheParkingTicketPayment(ticketStatus, chargeResponse, serviceFeeItem, marketplaceId,
            storeId, paymentResponse, paymentStatus);

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
                                         String marketplaceId, String storeId, PaymentOrderResponse paymentResponse,
                                         ParkingTicketAuthorizedPaymentStatus paymentStatus) {

    // prepare the ticket payment information
    Long ticketNumber = Long.parseLong(ticketStatus.getNumeroTicket());
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

    ParkinglotTicket parkinglotTicket = null;
    Optional<ParkinglotTicket> parkinglotTicketOpt = parkinglotTicketRepository.findByTicketNumber(ticketNumber);
    if (parkinglotTicketOpt.isPresent()) {
      parkinglotTicket = parkinglotTicketOpt.get();

    } else {
      parkinglotTicket = new ParkinglotTicket();
      parkinglotTicket.setTicketNumber(ticketNumber);
    }

    /*
     * Since each payment WPS doesn't store id for each payment, we are going to use the 'dataPagamento' as an id
     * when we need to get specific info about this specific payment.
     */
    parkinglotTicketPayment.setParkinglotTicket(parkinglotTicket);
    parkinglotTicketPayment.setDate(-1L);
    parkinglotTicket.addPayment(parkinglotTicketPayment);
    parkinglotTicket = parkinglotTicketRepository.save(parkinglotTicket);

    for (int i = 0; i < parkinglotTicket.getPayments().size(); i++) {
      parkinglotTicketPayment = parkinglotTicket.getPayments().get(i);
      if (parkinglotTicketPayment.getDate() == -1) {
        /* Set the dataPagamento for future use, since this information is returned by the WPS. */
        parkinglotTicketPayment.setDate(paymentStatus.getStatus().getDataPagamento());
        break;
      }
    }

    // Store in the repository
    parkinglotTicketRepository.save(parkinglotTicket);
  }
}