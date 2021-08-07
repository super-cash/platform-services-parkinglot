package cash.super_.platform.service.parkinglot.ticket;

import cash.super_.platform.client.parkingplus.model.RetornoConsulta;
import cash.super_.platform.service.parkinglot.AbstractParkingLotProxyService;
import cash.super_.platform.service.parkinglot.model.ParkingTicketState;
import cash.super_.platform.service.parkinglot.model.ParkinglotTicketStateTransition;
import cash.super_.platform.service.parkinglot.model.ParkinglotTicket;
import cash.super_.platform.service.parkinglot.repository.ParkinglotTicketRepository;
import cash.super_.platform.service.parkinglot.repository.TestingParkingLotStatusInMemoryRepository;
import cash.super_.platform.utils.DateTimeUtil;
import cash.super_.platform.utils.IsNumber;
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

  /**
   * The total number of states when the ticket is first scanned.
   */
  private static final int INITIAL_NUMBER_OF_STATES = 3;

  @Autowired
  private ParkinglotTicketRepository parkinglotTicketRepository;

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
    final Long validTicketNumber = IsNumber.stringIsLongWithException(ticketNumber, "Número Ticket");

    final Long storeId = supercashRequestContext.getStoreId();
    final Long userId = supercashRequestContext.getUserId();

    // TODO: Try to run this in a separate thread, use SpringEvents to decouple from the Request thread?
//    Runnable ticketStatusUpdater = () -> {
      // Verify if the ticket is new and just got scanned, and if so, it has 3 initial states
      Optional<ParkinglotTicket> parkinglotTicketSearch = parkinglotTicketRepository.findByTicketNumberAndUserIdAndStoreId(validTicketNumber, userId, storeId);

      ParkinglotTicket parkinglotTicket = null;
      if (parkinglotTicketSearch.isPresent()) {
        // the ticket has been created before
        parkinglotTicket = parkinglotTicketSearch.get();

        // When the status is requested with scanned, it means the user scanned the ticket again or in any other device
        if (scanned) {
          parkinglotTicket.addTicketStateTransition(ParkingTicketState.SCANNED, userId, DateTimeUtil.getMillis(LocalDateTime.now()));
        }

      } else {
        // The user just scanned the ticket for the first time, store the initial states
        parkinglotTicket = new ParkinglotTicket();
        parkinglotTicket.setTicketNumber(Long.valueOf(ticketNumber));
        parkinglotTicket.setUserId(userId);
        parkinglotTicket.setStoreId(storeId);
        parkinglotTicket.setCreatedAt(ticketStatus.getDataDeEntrada());
        parkinglotTicket.addTicketStateTransition(ParkingTicketState.PICKED_UP, userId, ticketStatus.getDataDeEntrada());
        parkinglotTicket.addTicketStateTransition(ParkingTicketState.SCANNED, userId, DateTimeUtil.getMillis(LocalDateTime.now()));

        // Adding the grace period
        LocalDateTime creationTime = DateTimeUtil.getLocalDateTime(parkinglotTicket.getCreatedAt());
        parkinglotTicket.addTicketStateTransition(ParkingTicketState.GRACE_PERIOD, userId,
                DateTimeUtil.getMillis(creationTime.plusMinutes(20)));

        // When saving the ticket for the first time while in the parking lot
        if (ticketStatus.getDataPermitidaSaidaUltimoPagamento() != null && ticketStatus.getDataPermitidaSaidaUltimoPagamento() > 0
                && ticketStatus.getTarifaPaga() != null && ticketStatus.getTarifaPaga() > 0) {

          final int MINUTES_TO_EXIT_PARKING_AFTER_PAYMENT = 20;
          LocalDateTime lastPaymentDateTime = LocalDateTime.now().minusMinutes(MINUTES_TO_EXIT_PARKING_AFTER_PAYMENT);
          parkinglotTicket.addTicketStateTransition(ParkingTicketState.PAID, userId,
                  DateTimeUtil.getMillis(lastPaymentDateTime));
        }
      }

      ParkingTicketState lastRecordedState = parkinglotTicket.getLastStateRecorded().getState();
      LOG.debug("Verifying current ticket {}'s last recoded state {} and to be saved {}; needs different: {}", ticketNumber, lastRecordedState, state, state != lastRecordedState);

      // add repeated state only if it's scanned and if it was not paid twice
      // NOT_PAID, SCANNED, SCANNED, PAID, NOT_PAID, SCANNED, PAID
      boolean stillInPaid = lastRecordedState == ParkingTicketState.PAID && ticketStatus.getTarifa() == ticketStatus.getTarifaPaga();
      if (state != lastRecordedState && !stillInPaid || (state == lastRecordedState && state == ParkingTicketState.SCANNED)) {
        // Save the current state at the time the user is in the parking lot (ticket still found by WPS)
        // Add 100 in the timestamp so it will be a bit higher on the first save
        parkinglotTicket.addTicketStateTransition(state, userId, DateTimeUtil.getMillis(LocalDateTime.now()) + 100);

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
    Long validTicketNumber = IsNumber.stringIsLongWithException(ticketNumber, "Número Ticket");
    ParkinglotTicket parkingTicket = new ParkinglotTicket();
    parkingTicket.setTicketNumber(validTicketNumber);

    // Verify if the ticket is new and just got scanned, and if so, it has 3 initial states
    Long storeId = supercashRequestContext.getStoreId();
    Long userId = supercashRequestContext.getUserId();

    Optional<ParkinglotTicket> ticketSearch = parkinglotTicketRepository.findByTicketNumberAndUserIdAndStoreId(validTicketNumber, userId, storeId);
    if (!ticketSearch.isPresent()) {
      LOG.error("Can't save the exit transition: parking ticket {} does not exit in storage", ticketNumber);
      return null;
    }

    // the ticket exists and so it loads all needed
    // TODO: This is to quickly fix tickets that were created before the state transitions
    ParkinglotTicket ticket = ticketSearch.get();
    if (ticket.getStates() == null || ticket.getStates().isEmpty()) {
      ticket.addTicketStateTransition(ParkingTicketState.PICKED_UP, userId, ticket.getCreatedAt());
      ticket.addTicketStateTransition(ParkingTicketState.SCANNED, userId, DateTimeUtil.getMillis(LocalDateTime.now()));

      // Adding the grace period
      LocalDateTime creationTime = DateTimeUtil.getLocalDateTime(ticket.getCreatedAt());
      ticket.addTicketStateTransition(ParkingTicketState.GRACE_PERIOD, userId,
              DateTimeUtil.getMillis(creationTime.plusMinutes(20)));

      // The date of the last payment made
      if (ticket.getPayments() != null && !ticket.getPayments().isEmpty()) {
        long lastPaymentDateMillis = ticket.getLastPaymentDateTimeMillis();
        LocalDateTime lastPaymentTime = DateTimeUtil.getLocalDateTime(lastPaymentDateMillis);
        ticket.addTicketStateTransition(ParkingTicketState.PAID, userId, DateTimeUtil.getMillis(lastPaymentTime));

      } else {
        // setting a payment time
        final int MINUTES_TO_EXIT_PARKING_AFTER_PAYMENT = 20;
        LocalDateTime lastPaymentDateTime = DateTimeUtil.getLocalDateTime(DateTimeUtil.getMillis(LocalDateTime.now())).minusMinutes(MINUTES_TO_EXIT_PARKING_AFTER_PAYMENT);

        // solves the problem of when it's the first time scanning, during tests, or when the ticket has never been scanned
        ticket.addTicketStateTransition(ParkingTicketState.PAID, userId, DateTimeUtil.getMillis(lastPaymentDateTime));
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
        default -> ParkingTicketState.EXITED_ON_GRACE_PERIOD;
      };

      // Save the ticket with the new state transition
      ticket.addTicketStateTransition(exitState, userId, DateTimeUtil.getMillis(LocalDateTime.now()));

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

    Optional<ParkinglotTicketStateTransition> transition = parkinglotTicket.getStates().stream()
            // https://www.geeksforgeeks.org/collections-reverseorder-java-examples/
            // https://stackoverflow.com/questions/32995559/reverse-a-comparator-in-java-8/54525172#54525172
            .sorted(Comparator.comparing(ParkinglotTicketStateTransition::getDate))
            .findFirst();

    // let's consider the user got in and left 20min ago, most conservative
    ticketStatus.dataDeEntrada(transition.isPresent() ? transition.get().getDate() : now - (1000 * 60 * 20));

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

    // Just a hack to save the last state in the ticket message to capture it on the return
    ticketStatus.setMensagem(parkinglotTicket.getStates().stream()
            // SORTED BY AT DESC
            .sorted(Comparator.comparing(ParkinglotTicketStateTransition::getDate, Collections.reverseOrder()))
            .findFirst()
            .get()
            .getState()
            .toString());

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
            ? entryDateTime.plusMinutes(TestingParkingLotStatusInMemoryRepository.GRACE_PERIOD_DURING_TESTING)
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
