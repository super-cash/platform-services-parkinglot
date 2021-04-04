package cash.super_.platform.service.parkinglot.ticket;

import cash.super_.platform.client.parkingplus.model.RetornoConsulta;
import cash.super_.platform.error.supercash.SupercashInvalidValueException;
import cash.super_.platform.service.pagarme.transactions.models.Item;
import cash.super_.platform.service.pagarme.transactions.models.TransactionRequest;
import cash.super_.platform.service.pagarme.transactions.models.TransactionResponseSummary;
import cash.super_.platform.service.parkinglot.model.ParkingTicketPayment;
import cash.super_.platform.service.parkinglot.model.ParkingTicketStatus;
import cash.super_.platform.service.parkinglot.payment.PagarmePaymentProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.common.base.Strings;
import brave.Span;
import brave.Tracer.SpanInScope;
import cash.super_.platform.client.parkingplus.model.PagamentoAutorizadoRequest;
import cash.super_.platform.client.parkingplus.model.PagamentoRequest;
import cash.super_.platform.client.parkingplus.model.RetornoPagamento;
import cash.super_.platform.service.parkinglot.AbstractParkingLotProxyService;
import cash.super_.platform.service.parkinglot.model.ParkingTicketAuthorizedPaymentStatus;
import cash.super_.platform.utils.SecretsUtil;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Proxy service to Retrieve the status of tickets, process payments, etc.
 * 
 * https://demonstracao.parkingplus.com.br/servicos/swagger-ui.html#!/servico-pagamento-ticket-2/pagarTicketAutorizadoUsingPOST
 *
 * @author marcellodesales
 * @author leandromsales
 *
 */
@Service
public class ParkingPlusTicketAuthorizePaymentProxyService extends AbstractParkingLotProxyService {

  private static final String BANDEIRA = "supercash";

  @Autowired
  PagarmePaymentProcessorService pagarmePaymentProcessorService;

  @Autowired
  private ParkingPlusTicketStatusProxyService statusService;

  public ParkingTicketAuthorizedPaymentStatus authorizePayment(String userId, String transactionId, TransactionRequest
          payRequest, TransactionResponseSummary payResponse) {
    LOG.debug("Payment auth request after Supercash payment request/response: {} {}", payRequest, payResponse);

    PagamentoAutorizadoRequest wpsAuthorizedPaymentRequest = new PagamentoAutorizadoRequest();

//    String saleIdStr = payResponse.getMetadata().get("sale_id");
//    if (saleIdStr != null) {
//      wpsAuthorizedPaymentRequest.setIdPromocao(Long.parseLong(saleIdStr));
//    }
    long saleIdProperty = properties.getSaleId();
    if (saleIdProperty >= 0) {
      wpsAuthorizedPaymentRequest.setIdPromocao(saleIdProperty);
    }

    wpsAuthorizedPaymentRequest.setBandeira(ParkingPlusTicketAuthorizePaymentProxyService.BANDEIRA);
    wpsAuthorizedPaymentRequest.setNumeroTicket(payRequest.getItems().get(0).getId());
    wpsAuthorizedPaymentRequest.setFaturado(true);
    wpsAuthorizedPaymentRequest.setIdGaragem(properties.getParkingLotId());
    wpsAuthorizedPaymentRequest.setPermitirValorExcedente(true);
    wpsAuthorizedPaymentRequest.setPermitirValorParcial(false);
    wpsAuthorizedPaymentRequest.setUdid(userId);
    wpsAuthorizedPaymentRequest.setValor(payRequest.getItems().get(0).getUnitPrice());
    wpsAuthorizedPaymentRequest.setIdTransacao(transactionId);

    return this.payAuthorizedTicket(wpsAuthorizedPaymentRequest);
  }

  private ParkingTicketAuthorizedPaymentStatus payAuthorizedTicket(PagamentoAutorizadoRequest
                                                                           pagamentoAutorizadoRequest) {
    RetornoPagamento authorizedPayment = null;
    Span newSpan = tracer.nextSpan().name("REST https://parkingplus.com.br/2/pagamentosEfetuados").start();
    try (SpanInScope spanScope = tracer.withSpanInScope(newSpan.start())) {
      String apiKey = SecretsUtil.makeApiKey(pagamentoAutorizadoRequest.getNumeroTicket(),
              pagamentoAutorizadoRequest.getUdid(), BANDEIRA, pagamentoAutorizadoRequest.getIdTransacao(),
              properties.getUserKey());

      LOG.info("Requesting authorization for payment: {}", pagamentoAutorizadoRequest);

      // Authorize the payment
      authorizedPayment = parkingTicketPaymentsApi.pagarTicketAutorizadoUsingPOST(apiKey, pagamentoAutorizadoRequest,
              properties.getApiKeyId());

      if (authorizedPayment == null) {
        LOG.error("Couldn't authorize payment in WPS. WPS request: {}", pagamentoAutorizadoRequest);
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

  public ParkingTicketAuthorizedPaymentStatus authorizePayment(String userId, String transactionId,
                                                               PagamentoAutorizadoRequest payRequest) {
    LOG.debug("Payment auth request: {}", payRequest);

    payRequest.setBandeira(ParkingPlusTicketAuthorizePaymentProxyService.BANDEIRA);
    payRequest.setFaturado(true);
    payRequest.setIdGaragem(properties.getParkingLotId());
    payRequest.setPermitirValorExcedente(true);
    payRequest.setPermitirValorParcial(true);
    payRequest.setUdid(userId);
    payRequest.setIdTransacao(SecretsUtil.makeApiKey(payRequest.getNumeroTicket(), payRequest.getUdid(),
        String.valueOf(payRequest.getValor()), transactionId));

    return this.payAuthorizedTicket(payRequest);
  }

  public ParkingTicketAuthorizedPaymentStatus authorizePayment(String userId, String transactionId,
                                                               PagamentoRequest payRequest) {
    LOG.debug("Payment auth request: {}", payRequest);

    if (Strings.isNullOrEmpty(payRequest.getEnderecoIp())) {
      throw new SupercashInvalidValueException("The client's device ip address must be provided");
    }

    // Credit card info
    if (payRequest.getCartaoDeCredito() == null || payRequest.getCartaoDeCredito() <= 0) {
      throw new SupercashInvalidValueException("The credit card number must be greater than 0");
    }

    if (Strings.isNullOrEmpty(payRequest.getCodigoDeSeguranca())) {
      throw new SupercashInvalidValueException("The credit card security code must be provided");
    }

    if (Strings.isNullOrEmpty(payRequest.getValidade())) {
      throw new SupercashInvalidValueException("The credit card expiration MMYYYY must be provided");
    }

    if (Strings.isNullOrEmpty(payRequest.getPortador())) {
      throw new SupercashInvalidValueException("The credit card full name must be provided");
    }

    RetornoPagamento authorizedPayment;
    payRequest.setBandeira(ParkingPlusTicketAuthorizePaymentProxyService.BANDEIRA);
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
      String apiKey = SecretsUtil.makeApiKey(payRequest.getNumeroTicket(), payRequest.getUdid(),
          payRequest.getEnderecoIp(), payRequest.getBandeira(), payRequest.getPortador(), payRequest.getIdTransacao(),
          properties.getUserKey());

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

  public ParkingTicketAuthorizedPaymentStatus process(ParkingTicketPayment paymentRequest, String userId,
                                                      String ticketId, String marketplaceId, String storeId,
                                                      String transactionId) {

    if (paymentRequest == null) {
      throw new SupercashInvalidValueException("The payment request must be provided.");
    }

    if (Strings.isNullOrEmpty(transactionId)) {
      throw new SupercashInvalidValueException("The transactionId must be provided in the request header.");
    }

    if (paymentRequest.getPayTicketRequest().getItems().size() == 0) {
      throw new SupercashInvalidValueException("You have to provide at least one item in this request.");
    }

    Map<String, String> metadata = paymentRequest.getPayTicketRequest().getMetadata();

    if (Strings.isNullOrEmpty(metadata.get("device_id"))) {
      throw new SupercashInvalidValueException("The key/value device_id field must be provided in the metadata.");
    }

    if (Strings.isNullOrEmpty(metadata.get("public_ip"))) {
      throw new SupercashInvalidValueException("The key/value public_ip field must be provided in the metadata.");
    }

    if (Strings.isNullOrEmpty(metadata.get("private_ip"))) {
      throw new SupercashInvalidValueException("The key/value private_ip field must be provided in the metadata.");
    }

    ParkingTicketAuthorizedPaymentStatus paymentStatus = null;
    String ticketNumber = "";
    if (paymentRequest.getPayTicketRequest() != null) {
      TransactionRequest request = paymentRequest.getPayTicketRequest();
      List<Item> items = request.getItems();

      if (items == null || items.size() == 0) {
        throw new SupercashInvalidValueException("At least one item must be provided.");
      }
      RetornoConsulta ticketStatus = isTicketAndAmountValid(userId, ticketId, items.get(0).getId(),
              items.get(0).getUnitPrice());

      paymentStatus = pagarmePaymentProcessorService.processPayment(paymentRequest.getPayTicketRequest(), ticketStatus,
              userId, marketplaceId, storeId, transactionId);

    } else if (paymentRequest.getAuthorizedRequest() != null) {
      PagamentoAutorizadoRequest request = paymentRequest.getAuthorizedRequest();
      isTicketAndAmountValid(userId, ticketId, request.getNumeroTicket(), request.getValor());
      paymentStatus = authorizePayment(userId, transactionId, request);

    } else if (paymentRequest.getRequest() != null) {
      PagamentoRequest request = paymentRequest.getRequest();
      isTicketAndAmountValid(userId, ticketId, request.getNumeroTicket(), request.getValor());
      paymentStatus = authorizePayment(userId, transactionId, request);

    } else {
      throw new SupercashInvalidValueException("You must provide a request, an authorizedRequest or a " +
              "payTicketRequest");
    }

    return paymentStatus;
  }

  protected RetornoConsulta isTicketAndAmountValid(String userId, String ticketId, String ticketNumber, int amount) {

    if (Strings.isNullOrEmpty(ticketNumber)) {
      throw new SupercashInvalidValueException("Ticket ID must be provided");
    }

    if (!ticketId.equals(ticketNumber)) {
      throw new SupercashInvalidValueException("The ticket number in the items[0].id must be equals to the URL" +
              " 'numeroTicket' parameter");
    }

    ParkingTicketStatus parkingTicketStatus = statusService.getStatus(userId, ticketId, amount, true, true,
            Optional.of(Long.valueOf(properties.getSaleId().longValue())));
    RetornoConsulta ticketStatus = parkingTicketStatus.getStatus();

    LOG.debug("Ticket status for {}: {}", ticketId, ticketStatus);

    return ticketStatus;
  }
}
