package cash.super_.platform.service.parkingplus;

import org.springframework.stereotype.Service;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import brave.Span;
import brave.Tracer.SpanInScope;
import cash.super_.platform.client.parkingplus.model.RetornoConsulta;
import cash.super_.platform.client.parkingplus.model.TicketRequest;
import cash.super_.platform.service.parkingplus.model.ParkingTicket;
import cash.super_.platform.service.parkingplus.model.ParkingTicketStatus;
import cash.super_.platform.service.parkingplus.util.SecretsUtil;

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

  public ParkingTicketStatus getStatus(ParkingTicket ticket) {
    LOG.debug("Looking for the status of ticket: {}", ticket);

    // Verify the input of addresses
    Preconditions.checkArgument(ticket != null, "The ticket must be provided");
    Preconditions.checkArgument(!Strings.isNullOrEmpty(ticket.getTicketNumber()), "Parking ticket number must be provided");

    TicketRequest request = new TicketRequest();
    request.setIdGaragem(1L);
    request.setNumeroTicket(ticket.getTicketNumber());
    request.setUdid(ticket.getUserId());

    RetornoConsulta ticketStatus;

    // Trace the google geo API Call
    // https://www.baeldung.com/spring-cloud-sleuth-single-application
    Span newSpan = tracer.nextSpan().name("REST https://parkingplus.com.br/2/tickets").start();
    newSpan.remoteServiceName("parkingplus");

    try (SpanInScope spanScope = tracer.withSpanInScope(newSpan.start())) {
      LOG.info("Requesting parking plus ticket status: {}", ticket);

      String apiKey = SecretsUtil.makeApiKey(ticket.getUserId(), properties.getUserKey());
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

    LOG.debug("Ticket status: {}", ticketStatus);
    return new ParkingTicketStatus(ticketStatus);
  }

}
