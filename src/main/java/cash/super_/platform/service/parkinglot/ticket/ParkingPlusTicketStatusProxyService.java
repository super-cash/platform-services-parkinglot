package cash.super_.platform.service.parkinglot.ticket;

import java.util.Optional;

import cash.super_.platform.error.supercash.SupercashInvalidValueException;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import brave.Span;
import brave.Tracer.SpanInScope;
import cash.super_.platform.client.parkingplus.model.RetornoConsulta;
import cash.super_.platform.client.parkingplus.model.TicketRequest;
import cash.super_.platform.service.parkinglot.AbstractParkingLotProxyService;
import cash.super_.platform.service.parkinglot.model.ParkingTicketStatus;
import cash.super_.platform.service.parkinglot.sales.ParkingPlusParkingSalesCachedProxyService;
import cash.super_.platform.utils.JsonUtil;
import cash.super_.platform.utils.SecretsUtil;

/**
 * Retrieve the status of tickets, process payments, etc.
 *
 * https://demonstracao.parkingplus.com.br/servicos/swagger-ui.html#!/servico-pagamento-ticket-2/getTicketUsingPOST
 *
 * @author marcellodesales
 *
 */
@Service
public class ParkingPlusTicketStatusProxyService extends AbstractParkingLotProxyService {

  // TODO: Remove this once we are in production
  @Autowired
  private ParkingPlusParkingSalesCachedProxyService parkingSalesService;

  @Autowired
  private ParkingPlusTicketAuthorizePaymentProxyService paymentAuthService;

  public ParkingTicketStatus getStatus(String userId, String ticketId, Optional<Long> saleId) {
    LOG.debug("Looking for the status of ticket: {}", ticketId);

    if (Strings.isNullOrEmpty(userId)) {
      throw new SupercashInvalidValueException("User ID must be provided");
    }

    if (Strings.isNullOrEmpty(ticketId)) {
      throw new SupercashInvalidValueException("Ticket ID must be provided");
    }

    RetornoConsulta ticketStatus;

    TicketRequest request = new TicketRequest();
    request.setIdGaragem(properties.getParkingLotId());
    request.setNumeroTicket(ticketId);
    request.setUdid(userId);

// This logic is great, but is could be allow a client to set another sale_id
//    if (saleId.isPresent()) {
//      Long sid = saleId.get();
//      if (sid < 0) {
//        saleId = Optional.of(Long.valueOf(properties.getSaleId().longValue()));
//      }
//      // Paid tickets will resolve an an error
//      request.setIdPromocao(saleId.get());
//    } else {
//      request.setIdPromocao(Long.valueOf(properties.getSaleId().longValue()));
//    }
    long saleIdProperty = properties.getSaleId();
    if (saleIdProperty >= 0) {
      request.setIdPromocao(saleIdProperty);
    }

    // Trace the google geo API Call
    // https://www.baeldung.com/spring-cloud-sleuth-single-application
    Span newSpan = tracer.nextSpan().name("REST https://parkingplus.com.br/2/tickets").start();
    newSpan.remoteServiceName("parkinglot");

    try (SpanInScope spanScope = tracer.withSpanInScope(newSpan.start())) {
      LOG.info("Requesting parking plus ticket status: {}", ticketId);
      try {
        LOG.debug("Request is: {}", JsonUtil.toJson(request));
      } catch (JsonProcessingException jsonProcessingException) {
        jsonProcessingException.printStackTrace();
      }

      String apiKey = SecretsUtil.makeApiKey(userId, properties.getUserKey());

      LOG.debug("Request User ApiKey is: {}", properties.getUserKey());
      LOG.debug("Request ApiKey is: {}", apiKey);

      ticketStatus = parkingTicketPaymentsApi.getTicketUsingPOST(apiKey, request, properties.getApiKeyId());

      // For the tracer
      newSpan.tag("ticketValue", String.valueOf(ticketStatus.getTarifa()));
      newSpan.tag("ticketPaidValue", String.valueOf(ticketStatus.getTarifaPaga()));

    } catch (RuntimeException error) {
      LOG.error("Couldn't get the status of ticket: {}", error.getMessage());
      throw error;

    } finally {
      newSpan.finish();
    }

    try {
      LOG.debug("Ticket status: {}", JsonUtil.toJson(ticketStatus));

    } catch (JsonProcessingException jsonError) {
      LOG.error("Error getting ticket json", jsonError);
    }
    return new ParkingTicketStatus(ticketStatus);
  }

}
