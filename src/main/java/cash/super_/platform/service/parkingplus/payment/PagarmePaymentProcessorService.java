package cash.super_.platform.service.parkingplus.payment;

import brave.Tracer;
import cash.super_.platform.error.ParkingPlusPaymentNotApprovedException;
import cash.super_.platform.error.SupercashTransactionStatusNotExpectedException;
import cash.super_.platform.service.pagarme.transactions.models.*;
import cash.super_.platform.service.parkingplus.autoconfig.ParkingPlusProperties;
import cash.super_.platform.service.parkingplus.model.ParkingTicketAuthorizedPaymentStatus;
import cash.super_.platform.service.parkingplus.ticket.ParkingPlusTicketAuthorizePaymentProxyService;
import cash.super_.platform.service.parkingplus.util.IsNumber;
import cash.super_.platform.service.parkingplus.util.JsonUtil;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
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
  private ParkingPlusTicketAuthorizePaymentProxyService paymentAuthService;

  public ParkingTicketAuthorizedPaymentStatus processPayment(String userId, TransactionRequest payRequest) {

    Item item = payRequest.getItems().get(0);

    Map<String, String> metadata = payRequest.getMetadata();
    Preconditions.checkArgument(metadata.get("device_id") != null,
            "The key/value device_id field must be provifed in the metadata");
    Preconditions.checkArgument(metadata.get("ip") != null,
            "The key/value ip field must be provifed in the metadata");

    String fieldName;

    fieldName = "Sale ID";
    String fieldValue = metadata.get("sale_id");
    if (fieldValue != null) {
      IsNumber.stringIsLongWithException(fieldValue, fieldName);
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
    payRequest.setPaymentMethod(Transaction.PaymentMethod.CREDIT_CARD);
    payRequest.setCapture(true);
    payRequest.setAsync(false);

    TransactionResponseSummary transactionResponse = null;
    try {
      transactionResponse = pagarmeClientService.requestPayment(payRequest);
    } catch (FeignException.BadRequest badRequestException) {
      SupercashTransactionStatusNotExpectedException exception = JsonUtil.toObject(badRequestException.responseBody(),
              SupercashTransactionStatusNotExpectedException.class);
      LOG.error(exception.getMessage());
      throw exception;
    }

    if (transactionResponse.getStatus() == Transaction.Status.PAID) {
      return paymentAuthService.authorizePayment(userId, payRequest, transactionResponse);
    } else {
      ParkingPlusPaymentNotApprovedException exception = new ParkingPlusPaymentNotApprovedException(HttpStatus.FORBIDDEN,
              "Transaction status is " + transactionResponse.getStatus());
      LOG.error(exception.getMessage());
      throw exception;
    }
  }

}