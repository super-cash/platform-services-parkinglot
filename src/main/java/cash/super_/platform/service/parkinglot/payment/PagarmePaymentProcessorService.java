package cash.super_.platform.service.parkinglot.payment;

import cash.super_.platform.client.parkingplus.model.RetornoConsulta;
import cash.super_.platform.clients.payment.PaymentServiceApiClient;
import cash.super_.platform.error.ParkingPlusPaymentNotApprovedException;
import cash.super_.platform.error.supercash.SupercashUnknownHostException;
import cash.super_.platform.service.parkinglot.AbstractParkingLotProxyService;
import cash.super_.platform.service.parkinglot.model.ParkingTicketAuthorizedPaymentStatus;
import cash.super_.platform.service.parkinglot.model.ParkinglotTicket;
import cash.super_.platform.service.parkinglot.model.ParkinglotTicketPayment;
import cash.super_.platform.service.parkinglot.repository.ParkinglotTicketRepository;
import cash.super_.platform.service.parkinglot.repository.TransactionRepository;
import cash.super_.platform.service.parkinglot.ticket.ParkingPlusTicketAuthorizePaymentProxyService;
import cash.super_.platform.service.payment.model.TransactionResponseSummary;
import cash.super_.platform.service.payment.model.pagarme.*;
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
public class PagarmePaymentProcessorService extends AbstractParkingLotProxyService {

  protected static final Logger LOG = LoggerFactory.getLogger(PagarmePaymentProcessorService.class);

  @Autowired
  private PaymentServiceApiClient paymentServiceApiClient;

  @Autowired
  private BuildProperties buildProperties;

  @Autowired
  private ParkingPlusTicketAuthorizePaymentProxyService paymentAuthService;

  @Autowired
  private ParkinglotTicketRepository parkinglotTicketRepository;

  @Autowired
  private TransactionRepository transactionRepository;

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

    /* Calculate our client amount to receive, based on percentage negociated. */
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

    payRequest.setCapture(true);

    payRequest.setAsync(false);

    TransactionResponseSummary transactionResponseSummary = null;

    try {
      cash.super_.platform.service.payment.model.pagseguro.TransactionResponse transactionResponse =
              paymentServiceApiClient.requestPayment(payRequest.toPagseguroTransactionRequest());
      transactionResponseSummary = transactionResponse.summary();
      // TODO: we have to certify that WPS accept the payment, otherwise we have to inform the user and ask him
      // to go to the support center. This implementation have to be implemented in the
      // cash.super_.platform.clients.wps.errors.WPSErrorHandlercash.super_.platform.clients.wps.errors.WPSErrorHandler
    } catch (feign.RetryableException re) {
      if (re.getCause() instanceof UnknownHostException) {
        throw new SupercashUnknownHostException("Host '" + re.getCause().getMessage() + "' unknown.");
      }
      throw re;
    }

    // TODO: Review this and check if it is possible to reuse the transaction object
    /* Saving payment request into the database */
    Long ticketNumber = Long.parseLong(ticketStatus.getNumeroTicket());
    Optional<ParkinglotTicket> parkinglotTicketOpt = parkinglotTicketRepository.findByTicketNumber(ticketNumber);
    ParkinglotTicketPayment parkinglotTicketPayment = new ParkinglotTicketPayment();
    parkinglotTicketPayment.setAmount(transactionResponseSummary.getPaidAmount());
    parkinglotTicketPayment.setServiceFee(serviceFeeItem.getUnitPrice());
    parkinglotTicketPayment.setMarketplaceId(Long.valueOf(marketplaceId));
    parkinglotTicketPayment.setStoreId(Long.valueOf(storeId));
    parkinglotTicketPayment.setRequesterService(buildProperties.get("name"));

    Optional<Transaction> transactionOpt =
            transactionRepository.findById(transactionResponseSummary.getTransactionId());
    if (transactionOpt.isPresent()) {
      parkinglotTicketPayment.setTransactionResponse((TransactionResponse) transactionOpt.get());
    }

    ParkinglotTicket parkinglotTicket = null;
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

    /*
     * Since we activate a sync transaction, PAID should be expected, otherwise we need to return saying that the
     * transaction was not accepted.
     */
    if (transactionResponseSummary.getStatus() == Transaction.Status.PAID) {
      ParkingTicketAuthorizedPaymentStatus ap = paymentAuthService.authorizePayment(userId, payRequest,
              transactionResponseSummary);
      for (int i = 0; i < parkinglotTicket.getPayments().size(); i++) {
        parkinglotTicketPayment = parkinglotTicket.getPayments().get(i);
        if (parkinglotTicketPayment.getDate() == -1) {
          /* Set the dataPagamento for future use, since this information is returned by the WPS. */
          parkinglotTicketPayment.setDate(ap.getStatus().getDataPagamento());
          break;
        }
      }
      parkinglotTicketRepository.save(parkinglotTicket);
      return ap;
    } else {
      ParkingPlusPaymentNotApprovedException exception = new ParkingPlusPaymentNotApprovedException(HttpStatus.FORBIDDEN,
              "Transaction status is " + transactionResponseSummary.getStatus());
      LOG.error(exception.getMessage());
      throw exception;
    }
  }

}