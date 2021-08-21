package cash.super_.platform.service.parkinglot.ticket;

import cash.super_.platform.client.parkingplus.model.RetornoConsulta;
import cash.super_.platform.service.parkinglot.AbstractParkingLotProxyService;
import cash.super_.platform.model.parkinglot.ParkingTicketState;
import cash.super_.platform.model.parkinglot.ParkinglotTicketStateTransition;
import cash.super_.platform.model.parkinglot.ParkinglotTicket;
import cash.super_.platform.repository.ParkinglotTicketRepository;
import cash.super_.platform.repository.ParkinglotTicketStateTransitionsRepository;
import cash.super_.platform.service.parkinglot.ticket.testing.TestingParkingLotStatusInMemoryRepository;
import cash.super_.platform.util.DateTimeUtil;
import cash.super_.platform.util.NumberUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Retrieve the status of tickets, process payments, etc.
 *
 * https://demonstracao.parkingplus.com.br/servicos/swagger-ui.html#!/servico-pagamento-ticket-2/getTicketUsingPOST
 *
 * @author marcellodesales
 *
 */
@Service
public class ParkingTicketsStateTransitionService extends AbstractParkingLotProxyService {

  /**
   * The list of exit states when the ticket exits the parking lot
   */
  public static final EnumSet<ParkingTicketState> TICKET_EXIT_STATES = EnumSet.of(
          ParkingTicketState.EXITED_ON_FREE, ParkingTicketState.EXITED_ON_PAID, ParkingTicketState.EXITED_ON_GRACE_PERIOD);

  @Autowired
  private ParkinglotTicketRepository parkinglotTicketRepository;

  @Autowired
  private ParkinglotTicketStateTransitionsRepository parkinglotTicketStateTransitionsRepository;

  /**
   * To define the grace period before its actual value for client counters
   */
  public static final int GRACE_PERIOD_MINUS_SECONDS_OFFSET = 15;

  /**
   * Store the ticket state transition asynchronoysly
   * @param ticketStatus
   * @param state
   */
  public void saveTicketTransitionStateWhileUserInLot(RetornoConsulta ticketStatus, final ParkingTicketState state, boolean scanned) {
    final String ticketNumber = ticketStatus.getNumeroTicket();
    final Long validTicketNumber = NumberUtil.stringIsLongWithException(ticketNumber, "Número Ticket");

    final Long storeId = supercashRequestContext.getStoreId();
    final Long userId = supercashRequestContext.getUserId();

    // TODO: Try to run this in a separate thread, use SpringEvents to decouple from the Request thread?
//    Runnable ticketStatusUpdater = () -> {
      // Verify if the ticket is new and just got scanned, and if so, it has 3 initial states
      // This is the current user in the session requesting
      Optional<ParkinglotTicket> requestingUserTicketSearch = parkinglotTicketRepository.findByTicketNumberAndUserIdAndStoreId(validTicketNumber, userId, storeId);

      ParkinglotTicket parkinglotTicket = null;
      ParkingTicketState lastRecordedState = null;

      // when the ticket is scanned by multiple people, just add one
      boolean alreadyScannedBySomeoneElse = false;
      if (requestingUserTicketSearch.isPresent()) {
        // the ticket has been created before
        parkinglotTicket = requestingUserTicketSearch.get();

        // When the status is requested with scanned, it means the user scanned the ticket again or in any other device
        if (scanned) {
          parkinglotTicket.addTicketStateTransition(ParkingTicketState.SCANNED, userId, storeId, DateTimeUtil.getMillis(LocalDateTime.now()));
        }

        if (parkinglotTicket.getLastStateRecorded() != null) {
          lastRecordedState = parkinglotTicket.getLastStateRecorded().getState();
          LOG.debug("Verifying current ticket {}'s last recoded state {} and to be saved {}; needs different: {}",
                  ticketNumber, lastRecordedState, state, state != lastRecordedState);
        }

      } else {
        // At this point the current user hasn't scanned this ticket, this might be a new user
        // if so, copy the states of the other ticket

        // The user just scanned the ticket for the first time, store the initial states
        parkinglotTicket = new ParkinglotTicket();
        parkinglotTicket.setTicketNumber(Long.valueOf(ticketNumber));
        parkinglotTicket.setUserId(userId);
        parkinglotTicket.setStoreId(storeId);

        if (parkinglotTicketRepository.existsDistinctByTicketNumberAndStoreId(validTicketNumber, storeId)) {
          // The ticket was picked up by someone else, so just add scanned

          if (scanned) {
            parkinglotTicket.addTicketStateTransition(ParkingTicketState.SCANNED, userId, storeId, DateTimeUtil.getMillis(LocalDateTime.now()));
            alreadyScannedBySomeoneElse = true;


            Set<ParkingTicketState> exclusionLast = ParkinglotTicket.lastRecordedExclusionType();
            Optional<List<ParkinglotTicketStateTransition>> lastRecordByOther = parkinglotTicketStateTransitionsRepository
                    .findFirst1ByParkinglotTicket_TicketNumberAndStateNotInOrderByDateDesc(validTicketNumber, exclusionLast);

            // this might always be true at this point
            if (lastRecordByOther.isPresent()) {
              lastRecordedState = lastRecordByOther.get().iterator().next().getState();
              LOG.debug("Verifying current ticket {}'s last recoded state {} and to be saved {}; needs different: {}", ticketNumber, lastRecordedState, state, state != lastRecordedState);
            }
          }

        } else {
          // This current user scanned the ticket so it's the first time, let's set all the values
          parkinglotTicket.addTicketStateTransition(ParkingTicketState.PICKED_UP, userId, storeId, ticketStatus.getDataDeEntrada());
          parkinglotTicket.addTicketStateTransition(ParkingTicketState.SCANNED, userId, storeId, DateTimeUtil.getMillis(LocalDateTime.now()));
          // Adding the grace period
          LocalDateTime creationTime = DateTimeUtil.getLocalDateTime(ticketStatus.getDataDeEntrada());
          parkinglotTicket.addTicketStateTransition(ParkingTicketState.GRACE_PERIOD, userId, storeId,
                  DateTimeUtil.getMillis(creationTime.plusMinutes(20)));
        }

        parkinglotTicket.setCreatedAt(ticketStatus.getDataDeEntrada());

        // When saving the ticket for the first time while in the parking lot
        // Only applicable when not scanned by the same person so that it does't add the PAY for he/she
        if (!alreadyScannedBySomeoneElse && ticketStatus.getDataPermitidaSaidaUltimoPagamento() != null && ticketStatus.getDataPermitidaSaidaUltimoPagamento() > 0
                && ticketStatus.getTarifaPaga() != null && ticketStatus.getTarifaPaga() > 0) {

          final int MINUTES_TO_EXIT_PARKING_AFTER_PAYMENT = 20;
          LocalDateTime lastPaymentDateTime = LocalDateTime.now().minusMinutes(MINUTES_TO_EXIT_PARKING_AFTER_PAYMENT);
          parkinglotTicket.addTicketStateTransition(ParkingTicketState.PAID, userId, storeId, DateTimeUtil.getMillis(lastPaymentDateTime));
        }
      }

    boolean stillInPaid = lastRecordedState == ParkingTicketState.PAID && ticketStatus.getTarifa() == ticketStatus.getTarifaPaga();
    if (alreadyScannedBySomeoneElse) {

      // Since someone else paid, we can't have the status in the list of the new user, only if it's not in paid
      if (!stillInPaid) {
        // Save the current state at the time the user is in the parking lot (ticket still found by WPS)
        // Add 100 in the timestamp so it will be a bit higher on the first save
        parkinglotTicket.addTicketStateTransition(state, userId, storeId, DateTimeUtil.getMillis(LocalDateTime.now()) + 100);
      }

      LOG.debug("Saving new state {} for ticket {}", state, ticketNumber);
      // Save the ticket and the transitions
      parkinglotTicketRepository.save(parkinglotTicket);

      return;
    }

    // AT THIS POINT, THIS IS THE SAME USER

    // add repeated state only if it's scanned and if it was not paid twice
    // NOT_PAID, SCANNED, SCANNED, PAID, NOT_PAID, SCANNED, PAID
    // If it's scanned, it must have been by someone else.
    if (state != lastRecordedState && !stillInPaid || (state == lastRecordedState && state == ParkingTicketState.SCANNED)) {
      // Save the current state at the time the user is in the parking lot (ticket still found by WPS)
      // Add 100 in the timestamp so it will be a bit higher on the first save
      parkinglotTicket.addTicketStateTransition(state, userId, storeId, DateTimeUtil.getMillis(LocalDateTime.now()) + 100);

      LOG.debug("Saving new state {} for ticket {}", state, ticketNumber);
      // Save the ticket and the transitions
      parkinglotTicketRepository.save(parkinglotTicket);
    }
//    };
  }

  /**
   * Store the ticket state transition asynchronoysly when the user exits the parking lot.
   * This is identified when WPS throws 424 on a ticket that existed before.
   * @param ticketNumber is the ticket number
   */
  public ParkinglotTicket saveTicketTransitionStateAfterUserExits(String ticketNumber) {
    // Verify if the ticket is new and just got scanned, and if so, it has 3 initial states
    Long validTicketNumber = NumberUtil.stringIsLongWithException(ticketNumber, "Número Ticket");
    ParkinglotTicket parkingTicket = new ParkinglotTicket();
    parkingTicket.setTicketNumber(validTicketNumber);

    // Verify if the ticket is new and just got scanned, and if so, it has 3 initial states
    Long storeId = supercashRequestContext.getStoreId();
    Long userId = supercashRequestContext.getUserId();

    Optional<ParkinglotTicket> ticketSearch = parkinglotTicketRepository.findByTicketNumberAndUserIdAndStoreId(validTicketNumber, userId, storeId);
    if (!ticketSearch.isPresent()) {
      LOG.error("Can't save the exit transition: parking ticket {} does not exit in storage", ticketNumber);

      ticketSearch = parkinglotTicketRepository.findByTicketNumberAndStoreId(validTicketNumber, storeId);
      if (!ticketSearch.isPresent()) {
        LOG.error("Can't find the ticket from another user");

        return null;
      }
    }

    // the ticket exists and so it loads all needed
    // TODO: This is to quickly fix tickets that were created before the state transitions
    ParkinglotTicket ticket = ticketSearch.get();
    if (ticket.getStates() == null || ticket.getStates().isEmpty()) {
      ticket.addTicketStateTransition(ParkingTicketState.PICKED_UP, userId, storeId, ticket.getCreatedAt());
      ticket.addTicketStateTransition(ParkingTicketState.SCANNED, userId, storeId, DateTimeUtil.getMillis(LocalDateTime.now()));

      // Adding the grace period
      LocalDateTime creationTime = DateTimeUtil.getLocalDateTime(ticket.getCreatedAt());
      ticket.addTicketStateTransition(ParkingTicketState.GRACE_PERIOD, userId, storeId,
              DateTimeUtil.getMillis(creationTime.plusMinutes(20)));

      // The date of the last payment made
      if (ticket.getPayments() != null && !ticket.getPayments().isEmpty()) {
        long lastPaymentDateMillis = ticket.getLastPaymentDateTimeMillis();
        LocalDateTime lastPaymentTime = DateTimeUtil.getLocalDateTime(lastPaymentDateMillis);
        ticket.addTicketStateTransition(ParkingTicketState.PAID, userId, storeId, DateTimeUtil.getMillis(lastPaymentTime));

      } else {
        // setting a payment time
        final int MINUTES_TO_EXIT_PARKING_AFTER_PAYMENT = 20;
        LocalDateTime lastPaymentDateTime = DateTimeUtil.getLocalDateTime(DateTimeUtil.getMillis(LocalDateTime.now())).minusMinutes(MINUTES_TO_EXIT_PARKING_AFTER_PAYMENT);

        // solves the problem of when it's the first time scanning, during tests, or when the ticket has never been scanned
        ticket.addTicketStateTransition(ParkingTicketState.PAID, userId, storeId, DateTimeUtil.getMillis(lastPaymentDateTime));
      }
      // Save the ticket states
      parkinglotTicketRepository.save(ticket);
    }

    ParkinglotTicketStateTransition lastStateRecorded = ticket.getLastStateRecorded();

    // Verify if the last recorded state was an exit, if so, return it
    if (!TICKET_EXIT_STATES.contains(lastStateRecorded.getState())) {
      // https://www.baeldung.com/java-switch#1-the-new-switch-expression
      ParkingTicketState exitState = switch (lastStateRecorded.getState()) {
        case FREE -> ParkingTicketState.EXITED_ON_FREE;
        case PAID -> ParkingTicketState.EXITED_ON_PAID;
        default -> {
          yield ParkingTicketState.EXITED_ON_GRACE_PERIOD;
        }
      };

      // Save the ticket with the new state transition
      ticket.addTicketStateTransition(exitState, userId, storeId, DateTimeUtil.getMillis(LocalDateTime.now()));

      // Save the ticket and the transitions
      parkinglotTicketRepository.save(ticket);
    }

    // Ticket already has the exit transition recorded
    return ticket;
  }

  /**
   * The state of a query result when the ticket has exited the parking lot.
   * @param parkinglotTicket
   * @return
   */
  public RetornoConsulta makeNewTicketTransitionAfterUserExits(ParkinglotTicket parkinglotTicket) {
    RetornoConsulta ticketStatus = new RetornoConsulta();
    ticketStatus.setNumeroTicket(parkinglotTicket.getTicketNumber().toString());
    ticketStatus.setErrorCode(0);

    long now = DateTimeUtil.getMillis(LocalDateTime.now());
    ticketStatus.setDataConsulta(now);

    // The entrnce of the ticket
    Optional<ParkinglotTicketStateTransition> entranceDate = parkinglotTicket.getStates().stream()
            .filter( ticketTransision -> ticketTransision.getState() == ParkingTicketState.PICKED_UP)
            .findFirst();
    // let's consider the user got in and left 20min ago, most conservative
    ticketStatus.dataDeEntrada(entranceDate.isPresent() ? entranceDate.get().getDate() : now - (1000 * 60 * 20));

    // Total value paid the last time
    ticketStatus.setTarifaPaga(
            parkinglotTicket.getPayments().stream()
                    .mapToInt(payment -> payment.getAmount().intValue() + payment.getServiceFee().intValue())
                    .sum());

    ticketStatus.setTicketValido(false);

    // Values to -1 but the value paid
    ticketStatus.setTarifa(-1);
    ticketStatus.setTarifaSemDesconto(-1);
    ticketStatus.setValorDesconto(-1);

    Optional<ParkinglotTicketStateTransition> lastTransition = parkinglotTicket.getStates().stream()
            // SORTED BY AT DESC
            .sorted(Comparator.comparing(ParkinglotTicketStateTransition::getDate, Collections.reverseOrder()))
            .findFirst();
    // Just a hack to save the last state in the ticket message to capture it on the return
    ticketStatus.setMensagem(lastTransition.get().getState().toString());

    return ticketStatus;
  }

  /**
   * Calculates the ticket state
   * @param ticketStatus
   * @param allowedExitMillis
   * @return
   */
  public ParkingTicketState calculateTicketStatus(RetornoConsulta ticketStatus, long allowedExitMillis) {
    int ticketFee = ticketStatus.getTarifa();

    if (!ticketStatus.isTicketValido() && ticketStatus.getTarifa() == -1 && ticketStatus.getTarifaPaga() == 0) {
      // the value was added during the exit time
      return ParkingTicketState.valueOf(ticketStatus.getMensagem());
    }

    // ticket exited the parking lot, get the value from the message (hack from calculation)
    if (!ticketStatus.isTicketValido() && ticketStatus.getTarifaPaga() > 0 && ticketFee == -1) {
      ParkingTicketState exitParkingLotState = ParkingTicketState.valueOf(ticketStatus.getMensagem());
      ticketStatus.setMensagem(exitParkingLotState.toString());
      return exitParkingLotState;
    }

    ParkingTicketState parkingTicketState = ParkingTicketState.NOT_PAID;

    long queryDateTimeMillis = ticketStatus.getDataConsulta();
    long entryDateTimeMillis = ticketStatus.getDataDeEntrada();

    LocalDateTime queryDateTime = DateTimeUtil.getLocalDateTime(queryDateTimeMillis);
    LocalDateTime allowedExitDateTime = DateTimeUtil.getLocalDateTime(allowedExitMillis);
    LocalDateTime entryDateTime = DateTimeUtil.getLocalDateTime(entryDateTimeMillis);

    // Make the grace period valud based on the entry date time
    LocalDateTime gracePeriodTime = testingParkinglotTicketRepository.containsTicket(ticketStatus.getNumeroTicket())
            ? entryDateTime.plusMinutes(TestingParkingLotStatusInMemoryRepository.MIN_GRACE_PERIOD_DURING_TESTING)
            : entryDateTime.plusMinutes(properties.getGracePeriodInMinutes());
    LOG.debug("The ticket queryTime={} allowedExitTime={} gracePeriodTime={}", queryDateTime, allowedExitDateTime, gracePeriodTime);

    // decrease a couple of seconds because of client clocks and delay
    if (queryDateTime.isBefore(gracePeriodTime.minusSeconds(GRACE_PERIOD_MINUS_SECONDS_OFFSET))) {
      LOG.debug("The ticket queryTimeStamp={} is before gracePeriodTimeStamp={} so setting state to grace period",
              queryDateTime, gracePeriodTime);
      parkingTicketState = ParkingTicketState.GRACE_PERIOD;
    }

    // we should not charge the user if the fee is 0
    if (ticketFee == 0) {
      if (allowedExitMillis - entryDateTimeMillis < 0) {
        LOG.debug("The ticket is free because {}-{}={} < 0", allowedExitMillis, entryDateTimeMillis, allowedExitMillis - entryDateTimeMillis);
        parkingTicketState = ParkingTicketState.FREE;
      }

    } else {

      // the user is still in the parking lot after making a payment
      if (ticketStatus.getTarifa().intValue() == ticketStatus.getTarifaPaga().intValue()) {
        LOG.debug("The ticket {} is already paid and the user is still in the parking lot (since the ststus is still != 404)",
                ticketStatus.getNumeroTicket());
        parkingTicketState = ParkingTicketState.PAID;
      }
    }
    return parkingTicketState;
  }
}
