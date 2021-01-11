package cash.super_.platform.service.parkingplus.ticket;

import org.springframework.stereotype.Service;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import brave.Span;
import brave.Tracer.SpanInScope;
import cash.super_.platform.client.parkingplus.model.PagamentoAutorizadoRequest;
import cash.super_.platform.client.parkingplus.model.PagamentoRequest;
import cash.super_.platform.client.parkingplus.model.RetornoPagamento;
import cash.super_.platform.service.parkingplus.AbstractParkingLotProxyService;
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

  public ParkingTicketAuthorizedPaymentStatus authorizePayment(String userId, PagamentoAutorizadoRequest payRequest) {
    LOG.debug("Payment auth request: {}", payRequest);

    // Verify the input of addresses
    Preconditions.checkArgument(payRequest != null, "The payment request must be provided");
    Preconditions.checkArgument(!Strings.isNullOrEmpty(payRequest.getNumeroTicket()),
        "Ticket ID must be provided");
    Preconditions.checkArgument(payRequest.getValor() > 0,
        "The value of the ticket must be greater than 0");

    RetornoPagamento authorizedPayment;
    payRequest.setBandeira("supercash");
    payRequest.setFaturado(true);
    payRequest.setIdGaragem(properties.getParkingLotId());
    payRequest.setPermitirValorExcedente(true);
    payRequest.setPermitirValorParcial(true);
    payRequest.setUdid(userId);

    payRequest.setIdTransacao(SecretsUtil.makeApiKey(payRequest.getNumeroTicket(), payRequest.getUdid(),
        String.valueOf(payRequest.getValor())));

    // Trace the google geo API Call
    // https://www.baeldung.com/spring-cloud-sleuth-single-application
    Span newSpan = tracer.nextSpan().name("REST https://parkingplus.com.br/2/pagamentosEfetuados").start();
    try (SpanInScope spanScope = tracer.withSpanInScope(newSpan.start())) {
      long apiKeyId = properties.getApiKeyId();
      String apiKey = SecretsUtil.makeApiKey(new String[] {payRequest.getNumeroTicket(), payRequest.getUdid(),
          payRequest.getBandeira(), payRequest.getIdTransacao(), properties.getUserKey()});


      LOG.info("Requesting authorization for payment: {}", payRequest);

      // Authorize the payment
      authorizedPayment = parkingTicketPaymentsApi.pagarTicketAutorizadoUsingPOST(apiKey, payRequest, apiKeyId);

      if (authorizedPayment == null) {
        LOG.error("Couldn't authorize payment: " + payRequest);
        throw new IllegalStateException("Can't get the status of payments with query");
      }

      // For the tracer
      newSpan.tag("ticketNumber", String.valueOf(authorizedPayment.getNumeroTicket()));
      newSpan.tag("receipt", String.valueOf(authorizedPayment.getComprovante()));
      newSpan.tag("rpsNumber", String.valueOf(authorizedPayment.getRps()));

    } finally {
      newSpan.finish();
    }

    LOG.debug("Payment made: {}", authorizedPayment);
    return new ParkingTicketAuthorizedPaymentStatus(authorizedPayment);
  }

  public ParkingTicketAuthorizedPaymentStatus authorizePayment(String userId, PagamentoRequest payRequest) {
    LOG.debug("Payment auth request: {}", payRequest);

    // Verify the input of addresses
    Preconditions.checkArgument(payRequest != null, "The payment request must be provided");
    Preconditions.checkArgument(!Strings.isNullOrEmpty(payRequest.getNumeroTicket()),
        "Ticket ID must be provided");
    Preconditions.checkArgument(payRequest.getValor() > 0,
        "The ticket value must be greater than 0");
    Preconditions.checkArgument(!Strings.isNullOrEmpty(payRequest.getEnderecoIp()),
        "The client's device ip address must be provided");

    // Credit card info
    Preconditions.checkArgument(payRequest.getCartaoDeCredito() != null && payRequest.getCartaoDeCredito() > 0,
        "The credit card number must be greater than 0");
    Preconditions.checkArgument(!Strings.isNullOrEmpty(payRequest.getCodigoDeSeguranca()),
        "The credit card security code must be provided");
    Preconditions.checkArgument(!Strings.isNullOrEmpty(payRequest.getValidade()),
        "The credit card expiration MMYYYY must be provided");
    Preconditions.checkArgument(!Strings.isNullOrEmpty(payRequest.getPortador()),
        "The credit card full name must be provided");

    RetornoPagamento authorizedPayment;
    payRequest.setBandeira("supercash");
    payRequest.setIdGaragem(properties.getParkingLotId());
    payRequest.setPermitirValorExcedente(true);
    payRequest.setUdid(userId);

    payRequest.setIdTransacao(SecretsUtil.makeApiKey(payRequest.getNumeroTicket(), payRequest.getUdid(),
        String.valueOf(payRequest.getValor())));

    // Trace the google geo API Call
    // https://www.baeldung.com/spring-cloud-sleuth-single-application
    Span newSpan = tracer.nextSpan().name("REST https://parkingplus.com.br/2/pagamentosEfetuados").start();
    try (SpanInScope spanScope = tracer.withSpanInScope(newSpan.start())) {
      long apiKeyId = properties.getApiKeyId();
      String apiKey = SecretsUtil.makeApiKey(new String[] {payRequest.getNumeroTicket(), payRequest.getUdid(),
          payRequest.getEnderecoIp(), payRequest.getBandeira(), payRequest.getPortador(), payRequest.getIdTransacao(),
          properties.getUserKey()});

      LOG.info("Requesting authorization for payment: {}", payRequest);

      // Authorize the payment
      authorizedPayment = parkingTicketPaymentsApi.pagarTicketUsingPOST(apiKey, payRequest, apiKeyId);

      if (authorizedPayment == null) {
        LOG.error("Couldn't authorize payment: " + payRequest);
        throw new IllegalStateException("Can't get the status of payments with query");
      }

      // For the tracer
      newSpan.tag("ticketNumber", String.valueOf(authorizedPayment.getNumeroTicket()));
      newSpan.tag("receipt", String.valueOf(authorizedPayment.getComprovante()));
      newSpan.tag("rpsNumber", String.valueOf(authorizedPayment.getRps()));

    } finally {
      newSpan.finish();
    }

    LOG.debug("Payment made: {}", authorizedPayment);
    return new ParkingTicketAuthorizedPaymentStatus(authorizedPayment);
  }

}
