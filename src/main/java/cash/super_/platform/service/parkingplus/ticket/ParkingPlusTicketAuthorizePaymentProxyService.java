package cash.super_.platform.service.parkingplus.ticket;

import org.springframework.stereotype.Service;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import brave.Span;
import brave.Tracer.SpanInScope;
import cash.super_.platform.client.parkingplus.model.PagamentoAutorizadoRequest;
import cash.super_.platform.client.parkingplus.model.RetornoPagamento;
import cash.super_.platform.service.parkingplus.AbstractParkingLotProxyService;
import cash.super_.platform.service.parkingplus.model.ParkingTicketAuthorization;
import cash.super_.platform.service.parkingplus.model.ParkingTicketAuthorizedPaymentStatus;
import cash.super_.platform.service.parkingplus.util.SecretsUtil;

/**
 * Proxy service to Retrieve the status of tickets, process payments, etc.
 * 
 * https://demonstracao.parkingplus.com.br/servicos/swagger-ui.html#!/servico-pagamento-ticket-2/pagarTicketAutorizadoUsingPOST
 *
 * @author marcellodesales
 *
 */
@Service
public class ParkingPlusTicketAuthorizePaymentProxyService extends AbstractParkingLotProxyService {

  public ParkingTicketAuthorizedPaymentStatus authorizePayment(String userId, ParkingTicketAuthorization paymentAuthorization) {
    LOG.debug("Payment auth request: {}", paymentAuthorization);

    // Verify the input of addresses
    Preconditions.checkArgument(paymentAuthorization != null, "The ticket must be provided");
    Preconditions.checkArgument(paymentAuthorization.getRequest() != null,
        "Request object must be submitted");
    Preconditions.checkArgument(!Strings.isNullOrEmpty(paymentAuthorization.getRequest().getNumeroTicket()),
        "Ticket ID must be provided");
    Preconditions.checkArgument(!Strings.isNullOrEmpty(paymentAuthorization.getRequest().getIdTransacao()),
        "Transaction ID for payment must be submitted (and the same)");
    Preconditions.checkArgument(paymentAuthorization.getRequest().getValor() > 0,
        "The value of the ticket must be greater than 0");

    RetornoPagamento authorizedPayment;
    PagamentoAutorizadoRequest paymentRequest = paymentAuthorization.getRequest();
    paymentRequest.setBandeira("supercash");
    paymentRequest.setFaturado(true);
    paymentRequest.setIdGaragem(properties.getParkingLotId());
    paymentRequest.setPermitirValorExcedente(true);
    paymentRequest.setPermitirValorParcial(true);
    paymentRequest.setUdid(userId);
    paymentRequest.setValor(paymentAuthorization.getRequest().getValor());

    // Trace the google geo API Call
    // https://www.baeldung.com/spring-cloud-sleuth-single-application
    Span newSpan = tracer.nextSpan().name("REST https://parkingplus.com.br/2/pagamentosEfetuados").start();
    try (SpanInScope spanScope = tracer.withSpanInScope(newSpan.start())) {
      long apiKeyId = properties.getApiKeyId();
      String apiKey = SecretsUtil.makeApiKey(new String[] {paymentRequest.getNumeroTicket(), paymentRequest.getUdid(),
          paymentRequest.getBandeira(), paymentRequest.getIdTransacao(), properties.getUserKey()});


      LOG.info("Requesting authorization for payment: {}", paymentRequest);

      // Authorize the payment
      authorizedPayment = parkingTicketPaymentsApi.pagarTicketAutorizadoUsingPOST(apiKey, paymentRequest, apiKeyId);

      if (authorizedPayment == null) {
        LOG.error("Couldn't authorize payment: " + paymentRequest);
        throw new IllegalStateException("Can't get the status of payments with query");
      }

      // For the tracer
      newSpan.tag("ticketNumber", String.valueOf(authorizedPayment.getNumeroTicket()));
      newSpan.tag("receipt", String.valueOf(authorizedPayment.getComprovante()));
      newSpan.tag("rpsNumber", String.valueOf(authorizedPayment.getRps()));

    } finally {
      newSpan.finish();
    }

    LOG.debug("Payments made: {}", authorizedPayment);
    return new ParkingTicketAuthorizedPaymentStatus(authorizedPayment);
  }

}
