package cash.super_.platform.service.parkinglot.payment;

import cash.super_.platform.client.parkingplus.model.RetornoConsulta;
import cash.super_.platform.error.ParkingPlusPaymentNotApprovedException;
import cash.super_.platform.error.supercash.SupercashUnknownHostException;
import cash.super_.platform.service.pagarme.transactions.models.*;
import cash.super_.platform.service.parkinglot.AbstractParkingLotProxyService;
import cash.super_.platform.service.parkinglot.model.ParkingTicketAuthorizedPaymentStatus;
import cash.super_.platform.service.parkinglot.ticket.ParkingPlusTicketAuthorizePaymentProxyService;
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
  private PagarmeClientService pagarmeClientService;

  @Autowired
  private BuildProperties buildProperties;

  @Autowired
  private ParkingPlusTicketAuthorizePaymentProxyService paymentAuthService;

  public ParkingTicketAuthorizedPaymentStatus processPayment(TransactionRequest payRequest, RetornoConsulta ticketStatus,
                                                             String userId, String marketplaceId, String storeId,
                                                             String transactionId) {

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

    Integer total = payRequest.getAmount();

    /* Calculate our client amount to receive, based on percentage negociated. */
    double ourClientAmount = total * properties.getClientPercentage() / 100;
    ourClient.setAmount(Double.valueOf(ourClientAmount).intValue());

    /*
     * Calculate our amount to receive, based on percentage negociated and on the additional service fee.
     * Note that in case we loose any cents, we solve this by calculate any potencial remind.
     */
//    Integer usClientAmount = total * (parkingPlusProperties.getOurPercentage() / 100);
//    usClientAmount = usClientAmount + (total - ourClientAmount - usClientAmount);
//    us.setAmount(usClientAmount + parkingPlusProperties.getOurFee());
    us.setAmount(total - ourClient.getAmount() + properties.getOurFee());

    splitRules.add(ourClient);
    splitRules.add(us);

    Item item = payRequest.getItems().get(0);
    item.setQuantity(1);
    item.setTangible(false);
    item.setTitle(properties.getTicketItemTitle());

    Item tsItem = new Item();
    tsItem.setId("1");
    tsItem.setQuantity(1);
    tsItem.setTangible(false);
    tsItem.setTitle(properties.getServiceFeeItemTitle());
    tsItem.setUnitPrice(properties.getOurFee());
    payRequest.addItem(tsItem);

    payRequest.setAmount(payRequest.getAmount() + properties.getOurFee());

    payRequest.setSplitRules(splitRules);

    payRequest.setPaymentMethod(Transaction.PaymentMethod.CREDIT_CARD);

    payRequest.addMetadata("ticket_number", item.getId());
    payRequest.addMetadata("service_fee", tsItem.getUnitPrice().toString());
    payRequest.addMetadata("marketplace_id", marketplaceId);
    payRequest.addMetadata("store_id", storeId);
    payRequest.addMetadata("requester_service", buildProperties.get("name"));
    Long allowedExitDateTime = ticketStatus.getDataPermitidaSaidaUltimoPagamento();
    if (allowedExitDateTime == null) {
      allowedExitDateTime = ticketStatus.getDataPermitidaSaida();
    }
    payRequest.addMetadata("lapsed_time", String.valueOf(allowedExitDateTime.longValue()
            - ticketStatus.getDataDeEntrada().longValue()));

    payRequest.setCapture(true);

    payRequest.setAsync(false);

    TransactionResponseSummary transactionResponse = null;

    try {
      transactionResponse = pagarmeClientService.requestPayment(payRequest);

    } catch (feign.RetryableException re) {
      if (re.getCause() instanceof UnknownHostException) {
        throw new SupercashUnknownHostException("Host '" + re.getCause().getMessage() + "' unknown.");
      }
      throw re;
    }

    /*
     * Since we activate a sync transaction, PAID should be expected, otherwise we need to return that the
     * transaction was not accepted.
     */
    if (transactionResponse.getStatus() == Transaction.Status.PAID) {
      return paymentAuthService.authorizePayment(userId, transactionId, payRequest, transactionResponse);
    } else {
      ParkingPlusPaymentNotApprovedException exception = new ParkingPlusPaymentNotApprovedException(HttpStatus.FORBIDDEN,
              "Transaction status is " + transactionResponse.getStatus());
      LOG.error(exception.getMessage());
      throw exception;
    }
  }

}