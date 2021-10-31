package cash.super_.platform.service.parkinglot.ticket.parkingplus;

import java.time.LocalDateTime;
import java.util.Optional;

import cash.super_.platform.adapter.feign.SupercashErrorCode;
import cash.super_.platform.adapter.feign.SupercashSimpleException;
import cash.super_.platform.error.parkinglot.*;
import cash.super_.platform.service.parkinglot.AbstractParkingLotProxyService;
import cash.super_.platform.model.parkinglot.ParkingTicketState;
import cash.super_.platform.model.parkinglot.ParkinglotTicket;
import cash.super_.platform.service.parkinglot.ticket.ParkingTicketsStateTransitionService;
import cash.super_.platform.util.DateTimeUtil;
import cash.super_.platform.util.SecretsUtil;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import brave.Span;
import brave.Tracer.SpanInScope;
import cash.super_.platform.client.parkingplus.model.RetornoConsulta;
import cash.super_.platform.client.parkingplus.model.TicketRequest;
import cash.super_.platform.model.parkinglot.ParkingTicketStatus;
import cash.super_.platform.util.JsonUtil;
import org.springframework.web.bind.annotation.RequestParam;

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

  @Autowired
  ParkingTicketsStateTransitionService parkingTicketsStateTransitionService;

  public ParkingTicketStatus getStatus(Long parkinglotId, String ticketNumber, boolean scanned) {
    // TODO: THIS MUST ALSO VERIFY THE PARKINGLOT_ID == STORE_ID
    LOG.debug("Looking for the status of ticket={} at the parkinglotId={}", ticketNumber, parkinglotId);
    if (Strings.isNullOrEmpty(ticketNumber)) {
      throw new SupercashInvalidValueException("Ticket ID must be provided.");
    }

    // TODO Validate the store Id == parkinglot ID. It is not going to be because of the swagger endpoints must have
      // the storeID as a parameter, but it's now in the context...
    Long storeId = supercashRequestContext.getStoreId();
    if (storeId.longValue() != parkinglotId.longValue()) {
        LOG.error("Client calls must change: storeId={} must be the same as parkinglotId={}", storeId, parkinglotId);
        throw new SupercashInvalidValueException(String.format("StoreID=%s must be the same as " +
                "ParkinglotId=%s", storeId, parkinglotId));
    }

    Long userId = supercashRequestContext.getUserId();

    // load the ticket status or load a testing ticket
    final RetornoConsulta ticketStatus;
    if (testingParkinglotTicketRepository.containsTicket(ticketNumber)) {
      LOG.debug("LOADING Query TESTING TICKET STATUS: {}", ticketNumber);
      ticketStatus = testingParkinglotTicketRepository.getQueryResult(ticketNumber);
      LOG.debug("LOADED Query TICKET STATUS: {}: {}", ticketNumber, ticketStatus);

    } else {
      LOG.debug("Ticket not found in local cache. Preparing to request TICKET STATUS from WPS: ticket={} useId={}", ticketNumber, userId);
      ticketStatus = retrieveFromWPS(ticketNumber);
    }

    // The ticket is not a test one... We need to retrieve it from WPS
    long allowedExitEpoch = !ticketExitedParkingLot(ticketStatus)
            ? ParkingTicketStatus.calculateAllowedExitDateTime(ticketStatus)
            : 0;
    ParkingTicketState parkingTicketState = parkingTicketsStateTransitionService.calculateTicketStatus(
            ticketStatus, allowedExitEpoch);

    // For the testing tickets, just set the status computed
    if (testingParkinglotTicketRepository.containsTicket(ticketNumber)) {
      return testingParkinglotTicketRepository.getStatus(ticketNumber);
    }

    // Store the ticket state transition
    if (!ticketExitedParkingLot(ticketStatus)) {
      parkingTicketsStateTransitionService.saveTicketTransitionStateWhileUserInLot(ticketStatus, parkingTicketState, scanned);
      LocalDateTime entryDateTime = DateTimeUtil.getLocalDateTime(ticketStatus.getDataDeEntrada());
      LocalDateTime gracePeriodTime = entryDateTime.plusMinutes(properties.getGracePeriodInMinutes());
      return new ParkingTicketStatus(ticketStatus, parkingTicketState, DateTimeUtil.getMillis(gracePeriodTime));
    }

    // ticket exited parking lot
    return new ParkingTicketStatus(ticketStatus, parkingTicketState, -1);
  }

  private static boolean ticketExitedParkingLot(RetornoConsulta ticketStatus) {
    return !ticketStatus.isTicketValido();
  }

  /**
   * Fetches the ticket status from WPS
   * @param ticketNumber is the ticket number
   * @return The query result
   */
  private RetornoConsulta retrieveFromWPS(String ticketNumber) {
    TicketRequest request = new TicketRequest();
    request.setIdGaragem(properties.getParkingLotId());
    request.setNumeroTicket(ticketNumber);
    request.setUdid(makeWpsUniqueUserId());

    long saleIdProperty = properties.getSaleId();
    if (saleIdProperty >= 0) {
      request.setIdPromocao(saleIdProperty);
    }

    Span newSpan = tracer.nextSpan().name("REST https://parkingplus.com.br/2/tickets").start();
    newSpan.remoteServiceName("parkinglot");

    RetornoConsulta ticketStatus = null;
    try (SpanInScope spanScope = tracer.withSpanInScope(newSpan.start())) {
      LOG.info("Requesting parking lots ticket status: {}", ticketNumber);
      try {
        LOG.debug("Request is: {}", JsonUtil.toJson(request));

      } catch (JsonProcessingException jsonProcessingException) {
        String errorMessage = "Error serializing request when trying to get ticket status.";
        LOG.error(errorMessage, jsonProcessingException);
        throw new SupercashSimpleException(SupercashErrorCode.GENERIC_ERROR, HttpStatus.INTERNAL_SERVER_ERROR,
                "Error in JsonUtil.toJson.");
      }

      String apiKey = SecretsUtil.makeApiKey(makeWpsUniqueUserId(), properties.getUserKey());

      LOG.debug("Request User ApiKey is: {}", properties.getUserKey());
      LOG.debug("Request ApiKey is: {}", apiKey);

      ticketStatus = parkingTicketPaymentsApi.getTicketUsingPOST(apiKey, request, properties.getApiKeyId());

      // For the tracer
      newSpan.tag("ticketValue", String.valueOf(ticketStatus.getTarifa()));
      newSpan.tag("ticketPaidValue", String.valueOf(ticketStatus.getTarifaPaga()));

    } catch (RuntimeException error) {
      LOG.error("Couldn't get the status of ticket: {}", error.getMessage());

      // Verify if the ticket existed before by checking the third party error
      if (error instanceof SupercashThirdPartySystemException) {
        SupercashThirdPartySystemException thirdPartySystemException = (SupercashThirdPartySystemException)error;

        // Save the state transition error because the ticket existed before and the user left the parking lot
        // When that happens, the error_code from WPS is 1. In this case, the response should be 200.
        // https://gitlab.com/supercash/services/parking-lot-service/-/issues/2
        Object thirdPartyErrorCode = thirdPartySystemException.SupercashExceptionModel.getAdditionalFields().get("third_party_error_code");
        if (thirdPartyErrorCode != null && Integer.valueOf(thirdPartyErrorCode.toString()) == 1) {
          // The ticket actually was just removed but existed before, so it can't fail
          ParkinglotTicket savedTicket = parkingTicketsStateTransitionService.saveTicketTransitionStateAfterUserExits(ticketNumber);

          // Can't recover the ticket for some reason. It is not in the database
          if (savedTicket == null) {
            throw error;
          }

          // Make a new ticket status with the current status
          ticketStatus = parkingTicketsStateTransitionService.makeNewTicketTransitionAfterUserExits(savedTicket);

        } else {
          LOG.error("Ticket error from WPS error code for ticket={} errorCode={}", ticketNumber, thirdPartyErrorCode);
          throw error;
        }

        // throw any other error, that's not error code = 1 or 3 already handled
      } else throw error;

    } finally {
      newSpan.finish();
    }

    try {
      LOG.debug("Ticket status: {}", JsonUtil.toJson(ticketStatus));
      return ticketStatus;


    } catch (JsonProcessingException jsonError) {
      LOG.error("Error deserializing status ticket to json.", jsonError);
      return null;
    }
  }

}
