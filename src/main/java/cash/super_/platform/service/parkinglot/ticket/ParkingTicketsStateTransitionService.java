package cash.super_.platform.service.parkinglot.ticket;

import cash.super_.platform.client.parkingplus.model.RetornoConsulta;
import cash.super_.platform.error.supercash.*;
import cash.super_.platform.service.parkinglot.AbstractParkingLotProxyService;
import cash.super_.platform.service.parkinglot.model.ParkingTicketState;
import cash.super_.platform.service.parkinglot.model.ParkingTicketStateTransition;
import cash.super_.platform.service.parkinglot.model.ParkingTicketStatus;
import cash.super_.platform.service.parkinglot.model.ParkinglotTicket;
import cash.super_.platform.service.parkinglot.repository.ParkinglotTicketRepository;
import cash.super_.platform.service.parkinglot.repository.ParkinglotTicketStateTransitionsRepository;
import cash.super_.platform.utils.IsNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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

  public static final String TIMEZONE_AMERICA_SAO_PAULO = "America/Sao_Paulo";

  public static final EnumSet<ParkingTicketState> TICKET_EXIT_STATES = EnumSet.of(
          ParkingTicketState.EXITED_ON_FREE, ParkingTicketState.EXITED_ON_PAID, ParkingTicketState.EXITED_ON_GRACE_PERIOD);

  @Autowired
  private ParkinglotTicketRepository parkinglotTicketRepository;

  @Autowired
  private ParkinglotTicketStateTransitionsRepository ticketStateTransitionsRepository;
  /**
   * To define the grace period before its actual value for client counters
   */
  public static final int GRACE_PERIOD_MINUS_SECONDS_OFFSET = 15;

  private static long getMillis(LocalDateTime dateTime) {
    return dateTime.atZone(ZoneId.of(TIMEZONE_AMERICA_SAO_PAULO)).toInstant().toEpochMilli();
  }

  private static LocalDateTime getLocalDateTime(long milliseconds) {
    LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), TimeZone.getDefault().toZoneId());
    return dateTime.atZone(ZoneId.of("UTC"))
            .withZoneSameInstant(ZoneId.of(TIMEZONE_AMERICA_SAO_PAULO))
            .toLocalDateTime();
  }

  /**
   * Store the ticket state transition asynchronoysly
   * @param ticketStatus
   * @param state
   */
  public void saveTicketTransitionStateWhileUserInLot(RetornoConsulta ticketStatus, final ParkingTicketState state) {
    final String ticketNumber = ticketStatus.getNumeroTicket();
    Runnable myRunnable = () -> {
      // Verify if the ticket is new and just got scanned, and if so, it has 3 initial states
      Long validTicketNumber = IsNumber.stringIsLongWithException(ticketNumber, "Número Ticket");
      Optional<ParkinglotTicket> parkinglotTicketSearch = parkinglotTicketRepository.findByTicketNumber(validTicketNumber);

      ParkinglotTicket parkinglotTicket = null;
      if (parkinglotTicketSearch.isPresent()) {
        // the ticket has been created before
        parkinglotTicket = parkinglotTicketSearch.get();

      } else {
        // The user just scanned the ticket for the first time, store the initial states
        parkinglotTicket = new ParkinglotTicket();
        parkinglotTicket.setTicketNumber(Long.valueOf(ticketNumber));
        parkinglotTicket.addTicketStateTransition(ParkingTicketState.PICKED_UP, ticketStatus.getDataDeEntrada());
        parkinglotTicket.addTicketStateTransition(ParkingTicketState.SCANNED, getMillis(LocalDateTime.now()));
      }

      // Save the current state at the time the user is in the parking lot (ticket still found by WPS)
      parkinglotTicket.addTicketStateTransition(state, getMillis(LocalDateTime.now()));

      // Save the ticket and the transitions
      parkinglotTicketRepository.save(parkinglotTicket);

      List<ParkingTicketStateTransition> savedTransitions = ticketStateTransitionsRepository.findAllByParkinglotTicketOrderByAtAsc(parkinglotTicket);
      int currentSize = savedTransitions.size();
    };
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

    Optional<ParkinglotTicket> ticketSearch = parkinglotTicketRepository.findByTicketNumber(validTicketNumber);
    if (!ticketSearch.isPresent()) {
      LOG.error("Can't save the exit transition: parking ticket {} does not exit in storage", ticketNumber);
      return null;
    }

    // the ticket exists and so it loads all needed
    // TODO: This is to quickly fix tickets that were created before the state transitions
    ParkinglotTicket ticket = ticketSearch.get();
    if (ticket.getStates() == null || ticket.getStates().isEmpty()) {
      ticket.addTicketStateTransition(ParkingTicketState.PICKED_UP, ticket.getCreatedAt());
      ticket.addTicketStateTransition(ParkingTicketState.SCANNED, getMillis(LocalDateTime.now()));
      LocalDateTime creationTime = getLocalDateTime(ticket.getCreatedAt());
      ticket.addTicketStateTransition(ParkingTicketState.GRACE_PERIOD, getMillis(creationTime.plusMinutes(20)));

      // The date of the last payment made
      if (ticket.getPayments() != null && !ticket.getPayments().isEmpty()) {
        long lastPaymentDateMillis = ticket.getLastPaymentDateTimeMillis();
        LocalDateTime lastPaymentTime = getLocalDateTime(lastPaymentDateMillis);
        ticket.addTicketStateTransition(ParkingTicketState.PAID, getMillis(lastPaymentTime));

      } else {
        ticket.addTicketStateTransition(ParkingTicketState.PAID, getMillis(LocalDateTime.now()));
      }
      // Save the ticket states
      parkinglotTicketRepository.save(ticket);
    }

    ParkingTicketStateTransition lastStateRecorded = ticket.getLastStateRecorded();

    // Verify if the last recorded state was an exit, if so, return it
    if (!TICKET_EXIT_STATES.contains(lastStateRecorded.getState())) {
      // https://www.baeldung.com/java-switch#1-the-new-switch-expression
      ParkingTicketState exitState = switch (lastStateRecorded.getState()) {
        case FREE -> ParkingTicketState.EXITED_ON_FREE;
        case PAID -> ParkingTicketState.EXITED_ON_PAID;
        default -> ParkingTicketState.EXITED_ON_GRACE_PERIOD;
      };

      // Save the ticket with the new state transition
      ticket.addTicketStateTransition(exitState, getMillis(LocalDateTime.now()));

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
  public RetornoConsulta makeNewTicketQueryAfterUserExits(ParkinglotTicket parkinglotTicket) {
    RetornoConsulta ticketStatus = new RetornoConsulta();
    ticketStatus.setNumeroTicket(parkinglotTicket.getTicketNumber().toString());
    ticketStatus.setErrorCode(0);
    ticketStatus.setDataConsulta(getMillis(LocalDateTime.now()));
    ticketStatus.dataDeEntrada(
            parkinglotTicket.getStates().stream()
                    // https://www.geeksforgeeks.org/collections-reverseorder-java-examples/
                    // https://stackoverflow.com/questions/32995559/reverse-a-comparator-in-java-8/54525172#54525172
            .sorted(Comparator.comparing(ParkingTicketStateTransition::getAt))
            .findFirst()
            .get()
            .getAt()
    );

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
            .sorted(Comparator.comparing(ParkingTicketStateTransition::getAt, Collections.reverseOrder()))
            .findFirst()
            .get()
            .getState()
            .toString());

    return ticketStatus;
  }

  /**
   * Calculates the ticket state
   * @param throwExceptionWhileValidating
   * @param ticketStatus
   * @param allowedExitEpoch
   * @return
   */
  public ParkingTicketState calculateTicketStatus(boolean throwExceptionWhileValidating, RetornoConsulta ticketStatus, long allowedExitEpoch) {
    int ticketFee = ticketStatus.getTarifa();
    int ticketFeePaid = ticketStatus.getTarifaPaga();
    String message = "";

    // ticket exited the parking lot, get the value from the message (hack from calculation)
    if (!ticketStatus.isTicketValido() && ticketStatus.getTarifaPaga() > 0 && ticketFee == -1) {
      ParkingTicketState exitParkingLotState = ParkingTicketState.valueOf(ticketStatus.getMensagem());
      ticketStatus.setMensagem("Ticket exited the parking lot: " + exitParkingLotState);
      return exitParkingLotState;
    }

    ParkingTicketState parkingTicketState = ParkingTicketState.NOT_PAID;

    long queryEpoch = ticketStatus.getDataConsulta();
    long entryEpoch = ticketStatus.getDataDeEntrada();

    // TODO Needs to be verified because it's local time
    LocalDateTime queryDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(queryEpoch), TimeZone.getDefault().toZoneId());
    LocalDateTime allowedExitDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(allowedExitEpoch), TimeZone.getDefault().toZoneId());
    LocalDateTime gracePeriodTime = ParkingTicketStatus.calculateGracePeriod(ticketStatus, properties.getGracePeriodInMinutes(), TIMEZONE_AMERICA_SAO_PAULO);

    LOG.debug("The ticket queryTime={} allowedExitTime={} gracePeriodTime={}", queryDateTime, allowedExitDateTime, gracePeriodTime);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LOG.debug("The ticket queryTimeStamp={} allowedExitTimeStamp={} gracePeriodTimeStamp={}",
            formatter.format(queryDateTime), formatter.format(allowedExitDateTime), formatter.format(gracePeriodTime));

    // decrease a couple of seconds because of client clocks and delay
    if (queryDateTime.isBefore(gracePeriodTime.minusSeconds(GRACE_PERIOD_MINUS_SECONDS_OFFSET))) {
      LOG.debug("The ticket queryTimeStamp={} is before gracePeriodTimeStamp={} so setting state to grace period",
              formatter.format(queryDateTime), formatter.format(gracePeriodTime));
      message += "You can leave the parking lot until " + allowedExitDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
      parkingTicketState = ParkingTicketState.GRACE_PERIOD;
    }

    // we should not charge the user if the fee is 0
    if (ticketFee == 0) {
      if (allowedExitEpoch - entryEpoch < 0) {
        message += "Today is free.";
        parkingTicketState = ParkingTicketState.FREE;
      }

      // Status message
      LOG.debug(message);

      SupercashAmountIsZeroException exception = new SupercashAmountIsZeroException(message);
      exception.addField("entry_date", entryEpoch);
      exception.addField("exit_allowed_date", allowedExitEpoch);
      if (throwExceptionWhileValidating) throw exception;

    } else {

      // the user is still in the parking lot after making a payment
      if (ticketFeePaid >= ticketFee && queryDateTime.isBefore(allowedExitDateTime)) {
        message = "The ticket is already paid and the user is still in the parking lot (since the ststus is still != 404)";
        LOG.debug(message);
        parkingTicketState = ParkingTicketState.PAID;
        if (throwExceptionWhileValidating) throw new SupercashPaymentAlreadyPaidException(message);
      }
    }
    return parkingTicketState;
  }
}
