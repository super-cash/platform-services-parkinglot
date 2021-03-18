package cash.super_.platform.service.parkingplus.ticket;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Preconditions;
import brave.Span;
import brave.Tracer.SpanInScope;
import cash.super_.platform.client.parkingplus.model.RetornoConsulta;
import cash.super_.platform.client.parkingplus.model.TicketRequest;
import cash.super_.platform.service.parkingplus.AbstractParkingLotProxyService;
import cash.super_.platform.service.parkingplus.model.ParkingTicketStatus;
import cash.super_.platform.service.parkingplus.sales.ParkingPlusParkingSalesCachedProxyService;
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

  public ParkingTicketStatus getStatus(String userId, String ticketId, Optional<Long> saleId) {
    LOG.debug("Looking for the status of ticket: {}", ticketId);

    RetornoConsulta ticketStatus;

    // Verify the input of addresses
    Preconditions.checkArgument(ticketId != null, "The ticket must be provided");

    TicketRequest request = new TicketRequest();
    request.setIdGaragem(1L);
    request.setNumeroTicket(ticketId);
    request.setUdid(userId);

    if (saleId.isPresent()) {
      Long sid = saleId.get();
      if (sid < 0) {
        saleId = Optional.of(Long.valueOf(properties.getSaleId().longValue()));
      }
      // Paid tickets will resolve an an error
      request.setIdPromocao(saleId.get());
    }

    // Trace the google geo API Call
    // https://www.baeldung.com/spring-cloud-sleuth-single-application
    Span newSpan = tracer.nextSpan().name("REST https://parkingplus.com.br/2/tickets").start();
    newSpan.remoteServiceName("parkingplus");

    try (SpanInScope spanScope = tracer.withSpanInScope(newSpan.start())) {
      LOG.info("Requesting parking plus ticket status: {}", ticketId);

      String apiKey = SecretsUtil.makeApiKey(userId, properties.getUserKey());
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
