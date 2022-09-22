package cash.super_.platform.service.parkinglot.ticket.parkingplus;

import java.util.*;

import cash.super_.platform.model.parkinglot.*;
import cash.super_.platform.service.parkinglot.AbstractParkingLotProxyService;
import cash.super_.platform.repository.ParkinglotTicketPaymentsRepository;
import cash.super_.platform.repository.ParkinglotTicketRepository;
import cash.super_.platform.util.SecretsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import brave.Span;
import brave.Tracer.SpanInScope;
import cash.super_.platform.client.parkingplus.model.PagamentoEfetuado;

/**
 * Proxy service to Retrieve the status of tickets, process payments, etc.
 * 
 * https://demonstracao.parkingplus.com.br/servicos/swagger-ui.html#!/servico-pagamento-ticket-2/pagamentosEfetuadosUsingGET
 *
 * @author marcellodesales
 *
 */
@Service
public class ParkingPlusTicketPaymentsProxyService extends AbstractParkingLotProxyService {

  @Autowired
  private ParkinglotTicketRepository parkinglotTicketRepository;

  @Autowired
  private ParkinglotTicketPaymentsRepository parkinglotTicketPaymentsRepository;

  /* TODO: Reimplement this method to get data from our database, instead of from WPS. */
  public ParkingTicketPaymentsMadeStatus getPaymentsMade(String userId, String marketplaceId, String storeId,
                                                         Optional<Integer> start, Optional<Integer> limit) {

    LOG.debug("Query the payments made by a user: {}", userId);

    ParkingTicketPaymentsMadeQuery paymentsMadeQuery = new ParkingTicketPaymentsMadeQuery();

    // Set the default user
    paymentsMadeQuery.setUserId(userId);
    paymentsMadeQuery.setPaginationLimit(!limit.isPresent() ? 0 : (limit.get() < 1 ? 10 : limit.get()));
    paymentsMadeQuery.setPaginationStart(!start.isPresent() ? 0 : (start.get() < 0) ? 0 : start.get());

    List<PagamentoEfetuado> paymentsMade = null;
    List<ParkingPaidTicketStatus> parkingPaidTicketStatuses = new ArrayList<>();

    // Trace the google geo API Call
    // https://www.baeldung.com/spring-cloud-sleuth-single-application
    Span newSpan = tracer.nextSpan().name("REST https://parkingplus.com.br/2/pagamentosEfetuados").start();
    try (SpanInScope spanScope = tracer.withSpanInScope(newSpan.start())) {
      LOG.info("Requesting payments made status query: {}", paymentsMadeQuery);

      String udid = paymentsMadeQuery.getUserId();
      long apiKeyId = properties.getApiKeyId();
      String apiKey = SecretsUtil.makeApiKey(udid, properties.getUserKey());

      paymentsMade = parkingTicketPaymentsApi.pagamentosEfetuadosUsingGET(apiKey, udid, String.valueOf(apiKeyId),
          paymentsMadeQuery.getPaginationStart(), paymentsMadeQuery.getPaginationLimit(), "");

      String ticketNumber = "";
      /* Getting ticket service fee */
      paymentsMade.forEach((pagamentoEfetuado) -> {
        ParkingPaidTicketStatus parkingPaidTicketStatus = new ParkingPaidTicketStatus(pagamentoEfetuado);

        Optional<ParkinglotTicket> parkinglotTicketOpt =
                parkinglotTicketRepository.findByTicketNumberAndStoreId(
                        Long.valueOf(parkingPaidTicketStatus.getTicket()),
                        Long.valueOf(storeId));
        if (parkinglotTicketOpt.isPresent()) {
          Optional<ParkinglotTicketPayment> parkinglotTicketPayment = parkinglotTicketPaymentsRepository
                  .findByDateAndParkinglotTicket(pagamentoEfetuado.getData(), parkinglotTicketOpt.get());
          if (parkinglotTicketPayment.isPresent()) {
            parkingPaidTicketStatus.setServiceFee(parkinglotTicketPayment.get().getServiceFee());
          }
        }
        parkingPaidTicketStatuses.add(parkingPaidTicketStatus);
      });

    } finally {
      newSpan.finish();
    }

    if (parkingPaidTicketStatuses.isEmpty()) {
      LOG.info("There's no payments for user {} ", userId);
    }

    LOG.debug("Payments made: {}", parkingPaidTicketStatuses);
    return new ParkingTicketPaymentsMadeStatus(parkingPaidTicketStatuses);
  }

}
