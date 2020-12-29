package cash.super_.platform.service.parkingplus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import brave.Span;
import brave.Tracer.SpanInScope;
import cash.super_.platform.client.parkingplus.model.RetornoConsulta;
import cash.super_.platform.client.parkingplus.model.TicketRequest;
import cash.super_.platform.service.parkingplus.model.ParkingTicket;
import cash.super_.platform.service.parkingplus.model.ParkingTicketStatus;
import cash.super_.platform.service.parkingplus.util.JsonUtil;
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

  // TODO: Remove this once we are in production
  @Autowired
  private ParkingPlusParkingSalesCachedProxyService parkingSalesService;

  public ParkingTicketStatus getStatus(String userId, ParkingTicket ticket) {
    LOG.debug("Looking for the status of ticket: {}", ticket);

    RetornoConsulta ticketStatus;

    // TODO: This is to keep testing while the server is down
    if (userId.contains("x-testing-x")) {
      ticketStatus = new RetornoConsulta();
      ticketStatus.setCnpjGaragem("14.207.662/0001-41");
      ticketStatus.setDataDeEntrada(1604080498000L);
      ticketStatus.setDataPermitidaSaida(1606964040000L);
      ticketStatus.setGaragem("GARAGEM A");
      ticketStatus.setIdGaragem(1L);
      ticketStatus.setMensagem("Saldo atual is under testing...");
      ticketStatus.setIdPromocao(ticket.getSaleId());
      ticketStatus.setNumeroTicket(ticket.getTicketNumber());
      ticketStatus.setPromocaoAtingida(false);
      ticketStatus.setPromocoesDisponiveis(true);
      ticketStatus.setSetor("ESTACIONAMENTO");
      ticketStatus.setTicketValido(true);

      int total = 52500;
      int discount = parkingSalesService.getSale(ticket.getSaleId()).getValorDesconto();

      ticketStatus.setTarifa(total - discount);
      ticketStatus.setTarifaPaga(0);
      ticketStatus.setTarifaSemDesconto(total);
      ticketStatus.setValorDesconto(discount);

      // return the testing service
      return new ParkingTicketStatus(ticketStatus);
    }

    // Verify the input of addresses
    Preconditions.checkArgument(ticket != null, "The ticket must be provided");
    Preconditions.checkArgument(!Strings.isNullOrEmpty(ticket.getTicketNumber()), "Parking ticket number must be provided");

    TicketRequest request = new TicketRequest();
    request.setIdGaragem(1L);
    request.setNumeroTicket(ticket.getTicketNumber());
    request.setUdid(userId);

    // Paid tickets will resolve an an error
    if (ticket.getSaleId() != null) {
      request.setIdPromocao(ticket.getSaleId());
    }

    // Trace the google geo API Call
    // https://www.baeldung.com/spring-cloud-sleuth-single-application
    Span newSpan = tracer.nextSpan().name("REST https://parkingplus.com.br/2/tickets").start();
    newSpan.remoteServiceName("parkingplus");

    try (SpanInScope spanScope = tracer.withSpanInScope(newSpan.start())) {
      LOG.info("Requesting parking plus ticket status: {}", ticket);

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
