package cash.super_.platform.service.parkingplus.payment;

import brave.Tracer;
import cash.super_.platform.service.pagarme.transactions.models.*;
import cash.super_.platform.service.parkingplus.autoconfig.ParkingPlusProperties;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

  public TransactionResponseSummary processPayment(String userId, TransactionRequest payRequest) {

    String ticketNumber = payRequest.getItems().get(0).getId();
    Preconditions.checkArgument(payRequest != null, "The payment request must be provided");
    Preconditions.checkArgument(!Strings.isNullOrEmpty(ticketNumber),
            "Ticket ID must be provided");
    Preconditions.checkArgument(payRequest.getAmount() > 0,
            "The value of the ticket must be greater than 0");
    Map<String, String> metadata = payRequest.getMetadata();
    Preconditions.checkArgument(metadata.get("device_id") != null, "The device_id metadata field must be provided");
    Preconditions.checkArgument(metadata.get("ip") != null, "The ip metadata field must be provided");
    Preconditions.checkArgument(metadata.get("lapsed_time") != null, "The lapsed_time must be provided");

    Item item = payRequest.getItems().get(0);
    item.setQuantity(1);
    item.setTangible(false);
    item.setTitle(parkingPlusProperties.getItemTitle());
    item.setUnitPrice(payRequest.getAmount());
    payRequest.addMetadata("ticket_number", item.getId());

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
    us.setChargeProcessingFee(false);

    Integer total = payRequest.getAmount();

    /* Calculate our client amount to receive, based on percentage negociated. */
    Integer ourClientAmount = total * (parkingPlusProperties.getClientPercentage() / 100);
    ourClient.setAmount(ourClientAmount);

    /*
     * Calculate our amount to receive, based on percentage negociated and on the additional service fee.
     * Note that in case we loose any cents, we solve this by calculate any potencial remind.
     */
    Integer usClientAmount = total * (parkingPlusProperties.getOurPercentage() / 100);
    usClientAmount = usClientAmount + (total - ourClientAmount - usClientAmount);
    us.setAmount(usClientAmount + parkingPlusProperties.getOurFee());

    splitRules.add(ourClient);
    splitRules.add(us);
    payRequest.setSplitRules(splitRules);

    payRequest.setPaymentMethod(Transaction.PaymentMethod.CREDIT_CARD);
    payRequest.setCapture(true);
    payRequest.setAsync(false);

    return pagarmeClientService.requestPayment(payRequest);
  }

}
