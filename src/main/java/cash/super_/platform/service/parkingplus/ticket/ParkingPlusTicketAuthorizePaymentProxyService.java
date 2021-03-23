package cash.super_.platform.service.parkingplus.ticket;

import cash.super_.platform.client.parkingplus.model.RetornoConsulta;
import cash.super_.platform.error.supercash.SupercashAmountIsZeroSimpleException;
import cash.super_.platform.error.supercash.SupercashInvalidValueSimpleException;
import cash.super_.platform.error.supercash.SupercashTransactionAlreadyPaidSimpleException;
import cash.super_.platform.service.pagarme.transactions.models.Item;
import cash.super_.platform.service.pagarme.transactions.models.TransactionRequest;
import cash.super_.platform.service.pagarme.transactions.models.TransactionResponseSummary;
import cash.super_.platform.service.parkingplus.model.ParkingTicketPayment;
import cash.super_.platform.service.parkingplus.model.ParkingTicketStatus;
import cash.super_.platform.service.parkingplus.payment.PagarmePaymentProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.common.base.Strings;
import brave.Span;
import brave.Tracer.SpanInScope;
import cash.super_.platform.client.parkingplus.model.PagamentoAutorizadoRequest;
import cash.super_.platform.client.parkingplus.model.PagamentoRequest;
import cash.super_.platform.client.parkingplus.model.RetornoPagamento;
import cash.super_.platform.service.parkingplus.AbstractParkingLotProxyService;
import cash.super_.platform.service.parkingplus.model.ParkingTicketAuthorizedPaymentStatus;
import cash.super_.platform.utils.SecretsUtil;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

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

  private static final String BANDEIRA = "supercash";

  @Autowired
  PagarmePaymentProcessorService pagarmePaymentProcessorService;

  @Autowired
  private ParkingPlusTicketStatusProxyService statusService;

  public ParkingTicketAuthorizedPaymentStatus authorizePayment(String userId, TransactionRequest
          payRequest, TransactionResponseSummary payResponse) {
    LOG.debug("Payment auth request after Supercash payment request/response: {} {}", payRequest,
            payResponse);

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
    wpsAuthorizedPaymentRequest.setIdTransacao(SecretsUtil.makeApiKey(wpsAuthorizedPaymentRequest.getNumeroTicket(),
            wpsAuthorizedPaymentRequest.getUdid(), String.valueOf(wpsAuthorizedPaymentRequest.getValor())));

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

  public ParkingTicketAuthorizedPaymentStatus authorizePayment(String userId, PagamentoAutorizadoRequest payRequest) {
    LOG.debug("Payment auth request: {}", payRequest);

    payRequest.setBandeira(ParkingPlusTicketAuthorizePaymentProxyService.BANDEIRA);
    payRequest.setFaturado(true);
    payRequest.setIdGaragem(properties.getParkingLotId());
    payRequest.setPermitirValorExcedente(true);
    payRequest.setPermitirValorParcial(true);
    payRequest.setUdid(userId);
    payRequest.setIdTransacao(SecretsUtil.makeApiKey(payRequest.getNumeroTicket(), payRequest.getUdid(),
        String.valueOf(payRequest.getValor())));

    return this.payAuthorizedTicket(payRequest);
  }

  public ParkingTicketAuthorizedPaymentStatus authorizePayment(String userId, PagamentoRequest payRequest) {
    LOG.debug("Payment auth request: {}", payRequest);

    if (Strings.isNullOrEmpty(payRequest.getEnderecoIp())) {
      throw new SupercashInvalidValueSimpleException("The client's device ip address must be provided");
    }

    // Credit card info
    if (payRequest.getCartaoDeCredito() == null || payRequest.getCartaoDeCredito() <= 0) {
      throw new SupercashInvalidValueSimpleException("The credit card number must be greater than 0");
    }

    if (Strings.isNullOrEmpty(payRequest.getCodigoDeSeguranca())) {
      throw new SupercashInvalidValueSimpleException("The credit card security code must be provided");
    }

    if (Strings.isNullOrEmpty(payRequest.getValidade())) {
      throw new SupercashInvalidValueSimpleException("The credit card expiration MMYYYY must be provided");
    }

    if (Strings.isNullOrEmpty(payRequest.getPortador())) {
      throw new SupercashInvalidValueSimpleException("The credit card full name must be provided");
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
                                                      String ticketId) {

    if (paymentRequest == null) {
      throw new SupercashInvalidValueSimpleException("The payment request must be provided");
    }

    ParkingTicketAuthorizedPaymentStatus paymentStatus = null;
    String ticketNumber = "";
    if (paymentRequest.getPayTicketRequest() != null) {
      TransactionRequest request = paymentRequest.getPayTicketRequest();
      List<Item> items = request.getItems();

      if (items == null || items.size() == 0) {
        throw new SupercashInvalidValueSimpleException("At least one item must be provided");
      }
      isTicketAndAmountValid(userId, ticketId, items.get(0).getId(), items.get(0).getUnitPrice());

      paymentStatus = pagarmePaymentProcessorService.processPayment(userId, paymentRequest.getPayTicketRequest());

    } else if (paymentRequest.getAuthorizedRequest() != null) {
      PagamentoAutorizadoRequest request = paymentRequest.getAuthorizedRequest();
      isTicketAndAmountValid(userId, ticketId, request.getNumeroTicket(), request.getValor());
      paymentStatus = authorizePayment(userId, request);

    } else if (paymentRequest.getRequest() != null) {
      PagamentoRequest request = paymentRequest.getRequest();
      isTicketAndAmountValid(userId, ticketId, request.getNumeroTicket(), request.getValor());
      paymentStatus = authorizePayment(userId, request);

    } else {
      throw new SupercashInvalidValueSimpleException("You must provide a request, an authorizedRequest or a " +
              "payTicketRequest");
    }

    return paymentStatus;
  }

  protected void isTicketAndAmountValid(String userId, String ticketId, String ticketNumber, int amount) {

    if (Strings.isNullOrEmpty(userId)) {
      throw new SupercashInvalidValueSimpleException("User ID must be provided");
    }

    if (Strings.isNullOrEmpty(ticketNumber)) {
      throw new SupercashInvalidValueSimpleException("Ticket ID must be provided");
    }

    if (!ticketId.equals(ticketNumber)) {
      throw new SupercashInvalidValueSimpleException("The ticket number in the items[0].id must be equals to the URL" +
              " 'numeroTicket' parameter");
    }

    ParkingTicketStatus parkingTicketStatus = statusService.getStatus(userId, ticketId,
            Optional.of(Long.valueOf(properties.getSaleId().longValue())));
    RetornoConsulta ticketStatus = parkingTicketStatus.getStatus();

    LOG.debug("Ticket status for {}: {}", ticketId, ticketStatus);

    int ticketFee = ticketStatus.getTarifa().intValue();
    int ticketFeePaid = ticketStatus.getTarifaPaga().intValue();
    String message = "";

    if (ticketFee == 0) {
      long entryDate = ticketStatus.getDataDeEntrada();
      Long exitAllowedDate = ticketStatus.getDataPermitidaSaidaUltimoPagamento();
      if (exitAllowedDate == null) {
        exitAllowedDate = ticketStatus.getDataPermitidaSaida();
      }
      if (exitAllowedDate.longValue() - entryDate < 0) {
        message += "Today is free.";
      } else {
        if (org.joda.time.Instant.now().getMillis() - entryDate <= properties.getGracePeriod()*1000) {
          LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(exitAllowedDate.longValue()),
                  TimeZone.getDefault().toZoneId());
          message += "You can go out until " + ldt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        }
      }
      LOG.debug(message);
      SupercashAmountIsZeroSimpleException exception = new SupercashAmountIsZeroSimpleException(message);
      exception.addField("entry_date", entryDate);
      exception.addField("exit_allowed_date", exitAllowedDate);
      throw exception;

    } else {
      if (ticketFee == ticketFeePaid) {
        message = "The ticket is already paid.";
        LOG.debug(message);
        throw new SupercashTransactionAlreadyPaidSimpleException(message);
      }

      if (amount != ticketFee) {
        message = "Amount has to be equal to ticket fee. Amount provided is " + amount + " and Ticket fee is " +
                ticketFee;
        LOG.debug(message);
        throw new SupercashInvalidValueSimpleException(message);
      }
    }
  }
}
