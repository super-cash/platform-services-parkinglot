package cash.super_.platform.service.parkinglot.ticket;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.TimeZone;

import cash.super_.platform.error.supercash.*;
import cash.super_.platform.service.parkinglot.AbstractParkingLotProxyService;
import cash.super_.platform.service.parkinglot.model.ParkingTicketState;
import cash.super_.platform.service.parkinglot.repository.TestingParkingLotStatusInMemoryRepository;
import cash.super_.platform.utils.SecretsUtil;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import brave.Span;
import brave.Tracer.SpanInScope;
import cash.super_.platform.client.parkingplus.model.RetornoConsulta;
import cash.super_.platform.client.parkingplus.model.TicketRequest;
import cash.super_.platform.service.parkinglot.model.ParkingTicketStatus;
import cash.super_.platform.service.parkinglot.sales.ParkingPlusParkingSalesCachedProxyService;
import cash.super_.platform.utils.JsonUtil;

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

  public static final String TIMEZONE_AMERICA_SAO_PAULO = "America/Sao_Paulo";

  @Autowired
  private ParkingPlusParkingSalesCachedProxyService parkingSalesService;

  @Autowired
  private ParkingPlusTicketAuthorizePaymentProxyService paymentAuthService;

  // Since it's only loaded in certain profiles, autowire is optional
  // https://stackoverflow.com/questions/57656119/how-to-autowire-conditionally-in-spring-boot/57656242#57656242
  @Autowired(required = false)
  private TestingParkingLotStatusInMemoryRepository testingParkinglotTicketRepository;

  public ParkingTicketStatus getStatus(String userId, String ticketNumber, Optional<Long> saleId) {
    return getStatus(userId, ticketNumber, -1, true, false, saleId);
  }

  public ParkingTicketStatus getStatus(String userId, String ticketNumber, long amount, boolean validate,
                                       boolean throwExceptionWhileValidating, Optional<Long> saleId) {
    LOG.debug("Looking for the status of ticket: {}", ticketNumber);
    if (Strings.isNullOrEmpty(ticketNumber)) {
      throw new SupercashInvalidValueException("Ticket ID must be provided.");
    }

    // load the ticket status or load a testing ticket
    RetornoConsulta ticketStatus = null;
    if (testingParkinglotTicketRepository.containsTicket(ticketNumber)) {
      LOG.debug("LOADING Query TESTING TICKET STATUS: {}", ticketNumber);
      ticketStatus = testingParkinglotTicketRepository.getQueryResult(ticketNumber);
      LOG.debug("LOADED Query TICKET STATUS: {}: {}", ticketNumber, ticketStatus);

    } else {
      LOG.debug("Preparing to request TICKET STATUS from WPS: ticket={} useId={}", ticketNumber, userId);
      ticketStatus = retrieveFromWPS(ticketNumber, userId);
    }

    // The ticket is not a test one... We need to retrieve it from WPS
    long allowedExitEpoch = ParkingTicketStatus.calculateAllowedExitDateTime(ticketStatus);
    ParkingTicketState parkingTicketState = calculateTicketStatus(throwExceptionWhileValidating, ticketStatus, allowedExitEpoch);

    // For the testing tickets, just set the status computed
    if (testingParkinglotTicketRepository.containsTicket(ticketNumber)) {
      return testingParkinglotTicketRepository.updateStatus(ticketNumber);
    }

    // There's nothing to validate yet
    //    if (!validate) {
    //      return new ParkingTicketStatus(ticketStatus);
    //    }

    LocalDateTime gracePeriodTime = ParkingTicketStatus.calculateGracePeriod(ticketStatus, properties.getGracePeriodInMinutes(), TIMEZONE_AMERICA_SAO_PAULO);
    return new ParkingTicketStatus(ticketStatus, parkingTicketState, getMillis(gracePeriodTime));
  }

  private static long getMillis(LocalDateTime dateTime) {
    return dateTime.atZone(ZoneId.of(TIMEZONE_AMERICA_SAO_PAULO)).toInstant().toEpochMilli();
  }

  private ParkingTicketState calculateTicketStatus(boolean throwExceptionWhileValidating, RetornoConsulta ticketStatus, long allowedExitEpoch) {
    int ticketFee = ticketStatus.getTarifa();
    int ticketFeePaid = ticketStatus.getTarifaPaga();
    String message = "";
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

    if (queryDateTime.isBefore(gracePeriodTime)) {
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

  /**
   * Fetches the ticket status from WPS
   * @param ticketNumber is the ticket number
   * @param userId is the userId
   * @return The query result
   */
  private RetornoConsulta retrieveFromWPS(String ticketNumber, String userId) {
    TicketRequest request = new TicketRequest();
    request.setIdGaragem(properties.getParkingLotId());
    request.setNumeroTicket(ticketNumber);
    request.setUdid(userId);

    long saleIdProperty = properties.getSaleId();
    if (saleIdProperty >= 0) {
      request.setIdPromocao(saleIdProperty);
    }

    Span newSpan = tracer.nextSpan().name("REST https://parkingplus.com.br/2/tickets").start();
    newSpan.remoteServiceName("parkinglot");

    RetornoConsulta ticketStatus;

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

      String apiKey = SecretsUtil.makeApiKey(userId, properties.getUserKey());

      LOG.debug("Request User ApiKey is: {}", properties.getUserKey());
      LOG.debug("Request ApiKey is: {}", apiKey);

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
      return ticketStatus;


    } catch (JsonProcessingException jsonError) {
      LOG.error("Error deserializing status ticket to json.", jsonError);
      return null;
    }
  }

  /**
   * Reset the testing tickets starte back to how they are bootstrapped
   */
  public void resetTestTickets(String transactionId, String marketplaceId, String userId) {
    LOG.info("Resetting testing tickets requested transactionId={} marketplaceId={} userId={}", transactionId, marketplaceId, userId);

    try {
      testingParkinglotTicketRepository.bootstrap();
      LOG.info("Finished resetting testing tickets requested transactionId={} userId={}", transactionId, userId);

    } catch (InterruptedException error) {
      LOG.error("Couldn't reset testing tickets requested transactionId={} userId={}: {}", transactionId, userId, error.getMessage());
      throw new IllegalStateException("Couldn't reset the state of the testing tickets: " + error.getMessage());
    }
  }

}
