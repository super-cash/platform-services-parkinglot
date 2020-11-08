package cash.super_.platform.service.parkingplus;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import brave.Span;
import brave.Tracer.SpanInScope;
import cash.super_.platform.client.parkingplus.model.PagamentoEfetuado;
import cash.super_.platform.service.parkingplus.model.ParkingTicketPaymentsMadeQuery;
import cash.super_.platform.service.parkingplus.model.ParkingTicketPaymentsMadeStatus;
import cash.super_.platform.service.parkingplus.util.SecretsUtil;

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

  public ParkingTicketPaymentsMadeStatus getPaymentsMade(ParkingTicketPaymentsMadeQuery paymentsMadeQuery) {
    LOG.debug("Query the payments made by a user: {}", paymentsMadeQuery);

    // Verify the input of addresses
    Preconditions.checkArgument(paymentsMadeQuery != null, "The ticket must be provided");
    Preconditions.checkArgument(!Strings.isNullOrEmpty(paymentsMadeQuery.getUserId()),
        "User ID must be provided to query payments");

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
      LOG.error("Couldn't get the payment status with query: " + paymentsMadeQuery);
      throw new IllegalStateException("Can't get the status of payments with query");
    }

    LOG.debug("Payments made: {}", paymentsMade);
    return new ParkingTicketPaymentsMadeStatus(paymentsMade);
  }

}
