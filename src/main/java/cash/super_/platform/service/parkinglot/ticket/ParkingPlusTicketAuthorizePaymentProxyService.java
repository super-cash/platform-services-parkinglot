package cash.super_.platform.service.parkinglot.ticket;

import cash.super_.platform.client.parkingplus.model.RetornoConsulta;
import cash.super_.platform.error.supercash.SupercashInvalidValueException;
import cash.super_.platform.error.supercash.SupercashSimpleException;
import cash.super_.platform.service.payment.model.pagarme.TransactionRequest;
import cash.super_.platform.service.parkinglot.AbstractParkingLotProxyService;
import cash.super_.platform.service.parkinglot.model.ParkingTicketPayment;
import cash.super_.platform.service.parkinglot.model.ParkingTicketStatus;
import cash.super_.platform.service.parkinglot.model.ParkinglotTicket;
import cash.super_.platform.service.parkinglot.payment.PaymentProcessorService;
import cash.super_.platform.service.parkinglot.repository.ParkinglotTicketRepository;
import cash.super_.platform.service.payment.model.supercash.PaymentResponseSummary;
import cash.super_.platform.service.payment.model.supercash.types.charge.AnonymousPaymentChargeRequest;
import cash.super_.platform.utils.IsNumber;
import cash.super_.platform.utils.SecretsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.common.base.Strings;
import brave.Span;
import brave.Tracer.SpanInScope;
import cash.super_.platform.client.parkingplus.model.PagamentoAutorizadoRequest;
import cash.super_.platform.client.parkingplus.model.PagamentoRequest;
import cash.super_.platform.client.parkingplus.model.RetornoPagamento;
import cash.super_.platform.service.parkinglot.model.ParkingTicketAuthorizedPaymentStatus;

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

  @Autowired
  PaymentProcessorService paymentProcessorService;

  @Autowired
  private ParkingPlusTicketStatusProxyService statusService;

  @Autowired
  private ParkinglotTicketRepository parkinglotTicketRepository;

  public ParkingTicketAuthorizedPaymentStatus authorizePayment(String userId, String ticketNumber, Long ticketPrice,
                                                               PaymentResponseSummary payResponse) {
    LOG.debug("Payment auth request after Supercash payment request/response: {} {}", ticketNumber, payResponse);

    PagamentoAutorizadoRequest wpsAuthorizedPaymentRequest = new PagamentoAutorizadoRequest();
    long saleIdProperty = properties.getSaleId();
    if (saleIdProperty >= 0) {
      wpsAuthorizedPaymentRequest.setIdPromocao(saleIdProperty);
    }

    wpsAuthorizedPaymentRequest.setBandeira(properties.getUdidPrefix());
    wpsAuthorizedPaymentRequest.setNumeroTicket(ticketNumber);
    wpsAuthorizedPaymentRequest.setFaturado(true);
    wpsAuthorizedPaymentRequest.setIdGaragem(properties.getParkingLotId());
    wpsAuthorizedPaymentRequest.setPermitirValorExcedente(true);
    wpsAuthorizedPaymentRequest.setPermitirValorParcial(false);
    wpsAuthorizedPaymentRequest.setUdid(userId);
    wpsAuthorizedPaymentRequest.setValor(ticketPrice.intValue());
    wpsAuthorizedPaymentRequest.setIdTransacao(this.generateTransactionId(ticketNumber));

    return this.authorizedPaidTicket(wpsAuthorizedPaymentRequest);
  }

  private ParkingTicketAuthorizedPaymentStatus authorizedPaidTicket(PagamentoAutorizadoRequest
                                                                           pagamentoAutorizadoRequest) {
    RetornoPagamento authorizedPayment;
    Span newSpan = tracer.nextSpan().name("REST https://parkingplus.com.br/2/pagamentosEfetuados").start();
    try (SpanInScope spanScope = tracer.withSpanInScope(newSpan.start())) {
      String apiKey = SecretsUtil.makeApiKey(pagamentoAutorizadoRequest.getNumeroTicket(),
              pagamentoAutorizadoRequest.getUdid(), properties.getUdidPrefix(), pagamentoAutorizadoRequest
                      .getIdTransacao(), properties.getUserKey());

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

  public ParkingTicketAuthorizedPaymentStatus authorizePayment(String userId, PagamentoRequest payRequest) {
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
    payRequest.setBandeira(properties.getUdidPrefix());
    payRequest.setIdGaragem(properties.getParkingLotId());
    payRequest.setPermitirValorExcedente(true);
    payRequest.setUdid(userId);
    payRequest.setIdTransacao(this.generateTransactionId(payRequest.getNumeroTicket()));

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

  public ParkingTicketAuthorizedPaymentStatus authorizePayment(String userId, PagamentoAutorizadoRequest payRequest) {
    LOG.debug("Payment auth request: {}", payRequest);

    payRequest.setBandeira(properties.getUdidPrefix());
    payRequest.setFaturado(true);
    payRequest.setIdGaragem(properties.getParkingLotId());
    payRequest.setPermitirValorExcedente(true);
    payRequest.setPermitirValorParcial(true);
    payRequest.setUdid(userId);
    payRequest.setIdTransacao(this.generateTransactionId(payRequest.getNumeroTicket()));

    return this.authorizedPaidTicket(payRequest);
  }

  public ParkingTicketAuthorizedPaymentStatus process(ParkingTicketPayment paymentRequest, String userId,
                                                      String ticketNumber, String marketplaceId, String storeId) {
    if (paymentRequest == null) {
      throw new SupercashInvalidValueException("The payment request must be provided.");
    }

    ParkingTicketAuthorizedPaymentStatus paymentStatus;
    if (paymentRequest.getPayTicketRequest() != null) {
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

      // pay ticket request (format version of pagarme to be parsed to pagseguro)
      TransactionRequest request = paymentRequest.getPayTicketRequest();

      RetornoConsulta ticketStatus = isTicketAndAmountValid(userId, ticketNumber, request.getAmount());
      paymentStatus = paymentProcessorService.processPayment(request, ticketStatus, userId, marketplaceId, storeId);

    } else if (paymentRequest.getAnonymousTicketPaymentRequest() != null) {
      Map<String, String> metadata = paymentRequest.getAnonymousTicketPaymentRequest().getMetadata();

      if (Strings.isNullOrEmpty(metadata.get("public_ip"))) {
        throw new SupercashInvalidValueException("The key/value public_ip field must be provided in the metadata.");
      }

      if (Strings.isNullOrEmpty(metadata.get("user_agent"))) {
        throw new SupercashInvalidValueException("The key/value user_agent field must be provided in the metadata.");
      }

      if (Strings.isNullOrEmpty(metadata.get("lapsed_time"))) {
        throw new SupercashInvalidValueException("The key/value lapsed_time field must be provided in the metadata.");
      }

      if (Strings.isNullOrEmpty(metadata.get("credit_card_issuer"))) {
        throw new SupercashInvalidValueException("The key/value credit_card_issuer field must be provided in the metadata.");
      }
      // anonymous ticket payment request (supercash format for anonymous payment request
      AnonymousPaymentChargeRequest request = paymentRequest.getAnonymousTicketPaymentRequest();
      RetornoConsulta ticketStatus = isTicketAndAmountValid(userId, ticketNumber, request.getAmount().getValue());
      paymentStatus = paymentProcessorService.processPayment(request, ticketStatus, userId, marketplaceId, storeId);

    } else if (paymentRequest.getAuthorizedRequest() != null) {
      throw new SupercashSimpleException("Direct request for WPS is currently disabled.");
      /* This is kept only for compatible reasons, since direct payment via WPS does not make sense anymore */
//      PagamentoAutorizadoRequest request = paymentRequest.getAuthorizedRequest();
//      isTicketAndAmountValid(userId, request.getNumeroTicket(), request.getValor());
//      paymentStatus = authorizePayment(userId, request);

    } else if (paymentRequest.getRequest() != null) {
      throw new SupercashSimpleException("Direct request for WPS is currently disabled.");
      /* This is kept only for compatible reasons, since direct payment via WPS does not make sense anymore */
//      PagamentoRequest request = paymentRequest.getRequest();
//      isTicketAndAmountValid(userId, request.getNumeroTicket(), request.getValor());
//      paymentStatus = authorizePayment(userId, request);

    } else {
      throw new SupercashInvalidValueException("You must provide a request, an authorizedRequest or a " +
              "payTicketRequest");
    }

    return paymentStatus;
  }

  protected RetornoConsulta isTicketAndAmountValid(String userId, String ticketNumber, long amount) {
    if (Strings.isNullOrEmpty(ticketNumber)) {
      throw new SupercashInvalidValueException("Ticket ID must be provided");
    }

    ParkingTicketStatus parkingTicketStatus = statusService.getStatus(userId, ticketNumber, amount, true, true,
            Optional.of(properties.getSaleId()));

    RetornoConsulta ticketStatus = parkingTicketStatus.getStatus();
    int ticketFee = ticketStatus.getTarifa();
    int ticketPaidCharge = ticketStatus.getTarifaPaga();

    // When the user pays the ticket multiple times, the value of ticketFee will always increase while the paid charge is the lumpsum of all payments
    int valueToBePaid = ticketFee - ticketPaidCharge;
    if (valueToBePaid != amount) {
        String message = "The amount has to be equal to ticket fee less the paid charge. amount=" + amount + " valueToBePaid=" + valueToBePaid;
        LOG.debug(message);
        throw new SupercashInvalidValueException(message);
    }

    LOG.debug("Ticket status for {}: {}", ticketNumber, ticketStatus);
    return ticketStatus;
  }

  private String generateTransactionId(String ticketNumber) {
    Optional<ParkinglotTicket> parkinglotTicketOpt = parkinglotTicketRepository.findById(IsNumber
            .stringIsLongWithException(ticketNumber, "Número Ticket"));
    String transactionId = ticketNumber + "-0";
    if (parkinglotTicketOpt.isPresent()) {
      transactionId = ticketNumber + "-" + parkinglotTicketOpt.get().getPayments().size();
    }
    LOG.debug("Transaction ID for ticket '{}': {}", ticketNumber, transactionId);
    return transactionId;
  }
}