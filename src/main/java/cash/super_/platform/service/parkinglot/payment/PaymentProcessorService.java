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
import cash.super_.platform.service.payment.model.supercash.types.charge.ChargePaymentMethodRequest;
import cash.super_.platform.service.payment.model.supercash.types.charge.ChargeStatus;
import cash.super_.platform.service.payment.model.supercash.types.charge.PaymentChargeResponse;
import cash.super_.platform.service.payment.model.supercash.types.charge.AnonymousPaymentChargeRequest;
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

// This logic is great, but is could be allow a client to set another sale_id
//    fieldName = "Sale ID";
//    String fieldValue = metadata.get("sale_id");
//    if (fieldValue != null) {
//      IsNumber.stringIsLongWithException(fieldValue, fieldName);
//    } else {
//      payRequest.addMetadata("sale_id", parkingPlusProperties.getSaleId().toString());
//    }
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
    Long allowedExitDateTime = ticketStatus.getDataPermitidaSaidaUltimoPagamento();
    if (allowedExitDateTime == null) {
      allowedExitDateTime = ticketStatus.getDataPermitidaSaida();
    }
    payRequest.addMetadata("lapsed_time", String.valueOf(allowedExitDateTime - ticketStatus.getDataDeEntrada()));

    payRequest.setCapture(false);

    payRequest.setAsync(false);

    PaymentOrderResponse paymentResponse = null;
    try {
      paymentResponse = paymentServiceApiClient.authorizePayment(payRequest.toSupercashPaymentOrderRequest());
      LOG.debug("PAYMENT RESPONSE: {}", paymentResponse);
    } catch (feign.RetryableException re) {
      if (re.getCause() instanceof UnknownHostException) {
        throw new SupercashUnknownHostException("Host '" + re.getCause().getMessage() + "' unknown.");
      }
      throw re;
    }

    if (paymentResponse != null && paymentResponse.getId() != null) {

      PaymentChargeResponse chargeResponse = paymentResponse.getCharges().stream().findFirst().get();

      ParkingTicketAuthorizedPaymentStatus parkingTicketAuthorizedPaymentStatus = null;
      if (chargeResponse.getStatus() == ChargeStatus.AUTHORIZED) {
        userId =  properties.getUdidPrefix() + "-" + marketplaceId + "-" + storeId + "-" + userId;
        parkingTicketAuthorizedPaymentStatus = paymentAuthService.authorizePayment(userId, ticketStatus.getNumeroTicket(),
                payRequest.getAmount(), paymentResponse.summary());
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
            parkinglotTicketPayment.setServiceFee(serviceFeeItem.getUnitPrice());
            parkinglotTicketPayment.setMarketplaceId(Long.valueOf(marketplaceId));
            parkinglotTicketPayment.setStoreId(Long.valueOf(storeId));
            parkinglotTicketPayment.setRequesterService(buildProperties.get("name"));

            Optional<Payment> paymentOpt = paymentRepository.findById(paymentResponse.getId());
            if (paymentOpt.isPresent()) {
              paymentResponse = (PaymentOrderResponse) paymentOpt.get();
              parkinglotTicketPayment.setPayment(paymentResponse);
            }
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
            throw exception;
          }
        }
      } else {
        throw new SupercashPaymentErrorException(HttpStatus.FORBIDDEN, "Payment method not authorized.");
      }
    }
    throw new SupercashPaymentErrorException("Parking lot service were unable to process payment gateway response. " +
            "The response id is null.");
  }
}