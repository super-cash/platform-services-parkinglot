package cash.super_.platform.service.parkingplus.ticket;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import brave.Span;
import brave.Tracer.SpanInScope;
import cash.super_.platform.client.parkingplus.model.PagamentoEfetuado;
import cash.super_.platform.service.parkingplus.AbstractParkingLotProxyService;
import cash.super_.platform.service.parkingplus.model.ParkingTicketPaymentsMadeQuery;
import cash.super_.platform.service.parkingplus.model.ParkingTicketPaymentsMadeStatus;
import cash.super_.platform.utils.SecretsUtil;

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

  public ParkingTicketPaymentsMadeStatus getPaymentsMade(String userId, Optional<Integer> start, Optional<Integer> limit) {
    LOG.debug("Query the payments made by a user: {}", userId);

    ParkingTicketPaymentsMadeQuery paymentsMadeQuery = new ParkingTicketPaymentsMadeQuery();

    // Set the default user
    paymentsMadeQuery.setUserId(userId);
    paymentsMadeQuery.setPaginationLimit(!limit.isPresent() ? 0 : (limit.get() < 1 ? 10 : limit.get()));
    paymentsMadeQuery.setPaginationStart(!start.isPresent() ? 0 : (start.get() < 0) ? 0 : start.get());

     List<PagamentoEfetuado> paymentsMade = new ArrayList<PagamentoEfetuado>();

    // Trace the google geo API Call
    // https://www.baeldung.com/spring-cloud-sleuth-single-application
    Span newSpan = tracer.nextSpan().name("REST https://parkingplus.com.br/2/pagamentosEfetuados").start();
    try (SpanInScope spanScope = tracer.withSpanInScope(newSpan.start())) {
      LOG.info("Requesting payments made status query: {}", paymentsMadeQuery);

      String udid = paymentsMadeQuery.getUserId();
      long apiKeyId = properties.getApiKeyId();
      String apiKey = SecretsUtil.makeApiKey(udid, properties.getUserKey());

      paymentsMade = parkingTicketPaymentsApi.pagamentosEfetuadosUsingGET(apiKey, udid, apiKeyId,
          paymentsMadeQuery.getPaginationStart(), paymentsMadeQuery.getPaginationLimit());

    } finally {
      newSpan.finish();
    }

    if (paymentsMade.isEmpty()) {
      LOG.error("There's no payments for user {} ", userId);
      throw new IllegalStateException("There's no payments for user=" + userId);
    }

    LOG.debug("Payments made: {}", paymentsMade);
    return new ParkingTicketPaymentsMadeStatus(paymentsMade);
  }

}
