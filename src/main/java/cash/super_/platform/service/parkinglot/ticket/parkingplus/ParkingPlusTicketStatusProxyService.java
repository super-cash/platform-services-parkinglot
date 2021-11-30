package cash.super_.platform.service.parkinglot.ticket.parkingplus;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import cash.super_.platform.adapter.feign.SupercashErrorCode;
import cash.super_.platform.adapter.feign.SupercashSimpleException;
import cash.super_.platform.error.parkinglot.*;
import cash.super_.platform.model.parkinglot.ParkinglotTicketStateTransition;
import cash.super_.platform.repository.ParkinglotTicketRepository;
import cash.super_.platform.repository.ParkinglotTicketStateTransitionsRepository;
import cash.super_.platform.service.parkinglot.AbstractParkingLotProxyService;
import cash.super_.platform.model.parkinglot.ParkingTicketState;
import cash.super_.platform.model.parkinglot.ParkinglotTicket;
import cash.super_.platform.service.parkinglot.ticket.ParkingTicketsStateTransitionService;
import cash.super_.platform.service.parkinglot.ticket.ParkinglotTicketsService;
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
  ParkinglotTicketsService parkinglotTicketsService;

  @Autowired
  private ParkinglotTicketRepository parkinglotTicketRepository;

  @Autowired
  ParkingTicketsStateTransitionService parkingTicketsStateTransitionService;

  @Autowired
  private ParkinglotTicketStateTransitionsRepository parkinglotTicketStateTransitionsRepository;

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
    Set<Long> scannedByOthers = new HashSet<>();
    long paidByUser = -1;
    if (testingParkinglotTicketRepository.containsTicket(ticketNumber)) {
      LOG.debug("LOADING Query TESTING TICKET STATUS: {}", ticketNumber);
      ticketStatus = testingParkinglotTicketRepository.getQueryResult(ticketNumber);
      LOG.debug("LOADED Query TICKET STATUS: {}: {}", ticketNumber, ticketStatus);

    } else {
        // If this ticket was scanned before, it should exist in one of these states
        Set<ParkingTicketState> exitStates = Set.of(
                ParkingTicketState.EXITED_ON_FREE, ParkingTicketState.EXITED_ON_PAID, ParkingTicketState.EXITED_ON_GRACE_PERIOD);
        Optional<ParkinglotTicketStateTransition> ticketAlreadyExited =
                parkinglotTicketStateTransitionsRepository.findFirstByTicketNumberAndStateIn(Long.valueOf(ticketNumber), exitStates);
        if (ticketAlreadyExited.isPresent()) {
          // Now, let's find out if the ticket was scanned by the current user or another one
            // 1. If it was scanned by the current user, use the current status
            // 2. If the ticket was scanned by someone else, create a copy with the same value
          Optional<String> ticketId = Optional.of(ticketNumber);
          Optional<List<ParkinglotTicket>> currentExitedTicket = parkinglotTicketRepository.findByTicketNumberAndStoreId(Long.valueOf(ticketNumber), storeId);
          if (currentExitedTicket.isPresent() && !currentExitedTicket.get().isEmpty()) {
              List<ParkinglotTicket> currentScans = currentExitedTicket.get();
              ParkinglotTicket cloneTicketFrom = null;
              for (ParkinglotTicket existingScan : currentScans) {
                  // Add all the existing IDs, no matter which one
                  scannedByOthers.add(existingScan.getUserId());

                  if (!existingScan.getPayments().isEmpty()) {
                      // Record the user who paid for the ticket
                      paidByUser = existingScan.getUserId();
                      // Clone from who paid for the ticket
                      cloneTicketFrom = existingScan;
                      // Record the list
                  }
                  // Just use the one from the user him/herself
                  if (existingScan.getUserId().equals(userId)) {
                      cloneTicketFrom = existingScan;
                  }
              }
              // Just remove the current user's as it's others
              scannedByOthers.remove(userId);

              // The ticket exited without payment... Clone from the first scan
              if (cloneTicketFrom == null) {
                  cloneTicketFrom = currentScans.get(0);
              }

              // If the user who scanned is different, we need to save a clone
              // Save the clone in case it existed, as the retrieval of the tickets will try to load this again
              if (cloneTicketFrom.getUserId().longValue() != userId.longValue()) {
                  parkinglotTicketsService.saveClone(cloneTicketFrom, storeId, userId);
              }
          }
          ParkinglotTicket exitedTicket = parkinglotTicketsService.retrieveTickets(parkinglotId, ticketId, null, null, null).stream().findFirst().get();
          ticketStatus = parkingTicketsStateTransitionService.makeNewTicketTransitionAfterUserExits(exitedTicket);

        } else {
          LOG.debug("Ticket not found in local cache. Preparing to request TICKET STATUS from WPS: ticket={} useId={}", ticketNumber, userId);
          ticketStatus = retrieveFromWPS(ticketNumber);
      }
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

    // TODO: this is a bad hack to record who has scanned the ticket before, but it is needed for th
    if (!scannedByOthers.isEmpty()) {
        String scannedByUsers = scannedByOthers.stream().map( number -> number.toString()).collect(Collectors.joining(","));
        ticketStatus.setMensagem(ticketStatus.getMensagem() + ",scannedByUsers=" + scannedByUsers);
        if (paidByUser > -1) {
            ticketStatus.setMensagem(ticketStatus.getMensagem() + ",paidByUser=" + paidByUser);
        }
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
