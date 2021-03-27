package cash.super_.platform.service.parkingplus.payment;

import brave.Tracer;
import cash.super_.platform.error.ParkingPlusPaymentNotApprovedSimpleException;
import cash.super_.platform.error.supercash.SupercashInvalidValueException;
import cash.super_.platform.error.supercash.SupercashTransactionStatusNotExpectedSimpleException;
import cash.super_.platform.service.pagarme.transactions.models.*;
import cash.super_.platform.service.parkingplus.autoconfig.ParkingPlusProperties;
import cash.super_.platform.service.parkingplus.model.ParkingTicketAuthorizedPaymentStatus;
import cash.super_.platform.service.parkingplus.ticket.ParkingPlusTicketAuthorizePaymentProxyService;
import cash.super_.platform.utils.IsNumber;
import cash.super_.platform.utils.JsonUtil;
import com.google.common.base.Strings;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Proxy service to Retrieve the status of tickets, process payments, etc.
 * 
 * https://demonstracao.parkingplus.com.br/servicos/swagger-ui.html#!/servico-pagamento-ticket-2/pagarTicketAutorizadoUsingPOST
 *
 * @author leandro
 *
 */
@Service
public class PagarmePaymentProcessorService {

  protected static final Logger LOG = LoggerFactory.getLogger(PagarmePaymentProcessorService.class);

  @Autowired
  protected Tracer tracer;

  @Autowired
  private PagarmeClientService pagarmeClientService;

  @Autowired
  private ParkingPlusProperties parkingPlusProperties;

  @Autowired
  private BuildProperties buildProperties;

  @Autowired
  private ParkingPlusTicketAuthorizePaymentProxyService paymentAuthService;

  public ParkingTicketAuthorizedPaymentStatus processPayment(TransactionRequest payRequest, String userId,
                                                             String marketplaceId, String storeId) {

    Item item = payRequest.getItems().get(0);

    Map<String, String> metadata = payRequest.getMetadata();

    if (Strings.isNullOrEmpty(metadata.get("device_id"))) {
      throw new SupercashInvalidValueException("The key/value device_id field must be provided in the metadata");
    }

    if (Strings.isNullOrEmpty(metadata.get("public_ip"))) {
      throw new SupercashInvalidValueException("The key/value public_ip field must be provided in the metadata");
    }

    if (Strings.isNullOrEmpty(metadata.get("private_ip"))) {
      throw new SupercashInvalidValueException("The key/value private_ip field must be provided in the metadata");
    }

    String fieldName;

// This logic is great, but is could be allow a client to set another sale_id
//    fieldName = "Sale ID";
//    String fieldValue = metadata.get("sale_id");
//    if (fieldValue != null) {
//      IsNumber.stringIsLongWithException(fieldValue, fieldName);
//    } else {
//      payRequest.addMetadata("sale_id", parkingPlusProperties.getSaleId().toString());
//    }
    long saleIdProperty = parkingPlusProperties.getSaleId();
    if (saleIdProperty >= 0) {
      payRequest.addMetadata("sale_id", parkingPlusProperties.getSaleId().toString());
    }

    fieldName = "CPF or CNPJ";
    IsNumber.stringIsLongWithException(payRequest.getCustomer().getDocuments().get(0).getNumber(), fieldName);

    fieldName = "CEP";
    IsNumber.stringIsLongWithException(payRequest.getBilling().getAddress().getZipcode(), fieldName);

    fieldName = "Card Number";
    IsNumber.stringIsLongWithException(payRequest.getCardNumber(), fieldName);

    fieldName = "Card CVV";
    IsNumber.stringIsLongWithException(payRequest.getCardCvv(), fieldName);

    fieldName = "Card Expiration Date";
    IsNumber.stringIsLongWithException(payRequest.getCardExpirationDate(), fieldName);

    List<SplitRule> splitRules = new ArrayList<>();
    SplitRule ourClient = new SplitRule();
    ourClient.setRecipientId(parkingPlusProperties.getClientRecipientId());
    ourClient.setLiable(true);
    ourClient.setChargeRemainderFee(true);
    ourClient.setChargeProcessingFee(false);
    SplitRule us = new SplitRule();
    us.setRecipientId(parkingPlusProperties.getOurRecipientId());
    us.setLiable(true);
    us.setChargeRemainderFee(true);
    us.setChargeProcessingFee(true);

    Integer total = payRequest.getAmount();

    /* Calculate our client amount to receive, based on percentage negociated. */
    double ourClientAmount = total * parkingPlusProperties.getClientPercentage() / 100;
    ourClient.setAmount(Double.valueOf(ourClientAmount).intValue());

    /*
     * Calculate our amount to receive, based on percentage negociated and on the additional service fee.
     * Note that in case we loose any cents, we solve this by calculate any potencial remind.
     */
//    Integer usClientAmount = total * (parkingPlusProperties.getOurPercentage() / 100);
//    usClientAmount = usClientAmount + (total - ourClientAmount - usClientAmount);
//    us.setAmount(usClientAmount + parkingPlusProperties.getOurFee());
    us.setAmount(total - ourClient.getAmount() + parkingPlusProperties.getOurFee());

    splitRules.add(ourClient);
    splitRules.add(us);

    item.setQuantity(1);
    item.setTangible(false);
    item.setTitle(parkingPlusProperties.getTicketItemTitle());

    Item tsItem = new Item();
    tsItem.setId("1");
    tsItem.setQuantity(1);
    tsItem.setTangible(false);
    tsItem.setTitle(parkingPlusProperties.getServiceFeeItemTitle());
    tsItem.setUnitPrice(parkingPlusProperties.getOurFee());
    payRequest.addItem(tsItem);

    payRequest.setAmount(payRequest.getAmount() + parkingPlusProperties.getOurFee());
    payRequest.setSplitRules(splitRules);
    payRequest.addMetadata("ticket_number", item.getId());
    payRequest.addMetadata("service_free", tsItem.getUnitPrice().toString());
    payRequest.addMetadata("marketplace_id", marketplaceId);
    payRequest.addMetadata("store_id", storeId);
    payRequest.addMetadata("requester_service", buildProperties.get("name"));
    payRequest.setPaymentMethod(Transaction.PaymentMethod.CREDIT_CARD);
    payRequest.setCapture(true);
    payRequest.setAsync(false);

    TransactionResponseSummary transactionResponse = null;
    try {
      transactionResponse = pagarmeClientService.requestPayment(payRequest);
    } catch (FeignException.BadRequest badRequestException) {
      SupercashTransactionStatusNotExpectedSimpleException exception = JsonUtil.toObject(badRequestException.responseBody(),
              SupercashTransactionStatusNotExpectedSimpleException.class);
      LOG.error(exception.getMessage());
      throw exception;
    }

    if (transactionResponse.getStatus() == Transaction.Status.PAID) {
      return paymentAuthService.authorizePayment(userId, payRequest, transactionResponse);
    } else {
      ParkingPlusPaymentNotApprovedSimpleException exception = new ParkingPlusPaymentNotApprovedSimpleException(HttpStatus.FORBIDDEN,
              "Transaction status is " + transactionResponse.getStatus());
      LOG.error(exception.getMessage());
      throw exception;
    }
  }

}