package cash.super_.platform.service.parkinglot.ticket;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import cash.super_.platform.service.parkinglot.model.ParkingPaidTicketStatus;
import cash.super_.platform.service.parkinglot.payment.PagarmeClientService;
import cash.super_.platform.utils.IsNumber;
import cash.super_.platform.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import brave.Span;
import brave.Tracer.SpanInScope;
import cash.super_.platform.client.parkingplus.model.PagamentoEfetuado;
import cash.super_.platform.service.parkinglot.AbstractParkingLotProxyService;
import cash.super_.platform.service.parkinglot.model.ParkingTicketPaymentsMadeQuery;
import cash.super_.platform.service.parkinglot.model.ParkingTicketPaymentsMadeStatus;
import cash.super_.platform.utils.SecretsUtil;
import springfox.documentation.spring.web.json.Json;

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
  private PagarmeClientService pagarmeClientService;

  public ParkingTicketPaymentsMadeStatus getPaymentsMade(String userId, Optional<Integer> start, Optional<Integer> limit) {
    LOG.debug("Query the payments made by a user: {}", userId);

    ParkingTicketPaymentsMadeQuery paymentsMadeQuery = new ParkingTicketPaymentsMadeQuery();

    // Set the default user
    paymentsMadeQuery.setUserId(userId);
    paymentsMadeQuery.setPaginationLimit(!limit.isPresent() ? 0 : (limit.get() < 1 ? 10 : limit.get()));
    paymentsMadeQuery.setPaginationStart(!start.isPresent() ? 0 : (start.get() < 0) ? 0 : start.get());

    List<PagamentoEfetuado> paymentsMade = new ArrayList<PagamentoEfetuado>();
    List<ParkingPaidTicketStatus> parkingPaidTicketStatuses = new ArrayList<>();

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

      paymentsMade.forEach((pagamentoEfetuado) -> {
        ParkingPaidTicketStatus parkingPaidTicketStatus = new ParkingPaidTicketStatus(pagamentoEfetuado);
        Map<String, String> paymentMetadata = null;
        try {
          paymentMetadata = pagarmeClientService.getTransactionMetadata("ticket_number",
                  pagamentoEfetuado.getTicket());
        } catch (feign.RetryableException re) {
          if (re.getCause() instanceof UnknownHostException) {
            LOG.error("Couldn't get service_fee for ticket {} due to unknown host exception: '{}'",
                    pagamentoEfetuado.getTicket(), re.getCause().getMessage());
          }
        }
        LOG.debug("paymentMetadata is: {}", paymentMetadata);
        if (paymentMetadata != null) {
          String serviceFeeKey = "service_fee";
          if (paymentMetadata.containsKey(serviceFeeKey) && IsNumber.stringIsLong(paymentMetadata.get(serviceFeeKey))) {
            parkingPaidTicketStatus.setServiceFee(Long.parseLong(paymentMetadata.get(serviceFeeKey)));
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
