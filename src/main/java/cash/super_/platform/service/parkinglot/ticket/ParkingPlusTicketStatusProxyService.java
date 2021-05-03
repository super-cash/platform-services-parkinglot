package cash.super_.platform.service.parkinglot.ticket;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.TimeZone;

import cash.super_.platform.error.supercash.*;
import cash.super_.platform.service.parkinglot.AbstractParkingLotProxyService;
import cash.super_.platform.service.parkinglot.model.SupercashTicketStatus;
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

  // TODO: Remove this once we are in production
  @Autowired
  private ParkingPlusParkingSalesCachedProxyService parkingSalesService;

  @Autowired
  private ParkingPlusTicketAuthorizePaymentProxyService paymentAuthService;

  public ParkingTicketStatus getStatus(String userId, String ticketNumber, Optional<Long> saleId) {
    return getStatus(userId, ticketNumber, -1, true, false, saleId);
  }

  public ParkingTicketStatus getStatus(String userId, String ticketNumber, long amount, boolean validate,
                                       boolean throwExceptionWhileValidating, Optional<Long> saleId) {
    LOG.debug("Looking for the status of ticket: {}", ticketNumber);

    if (Strings.isNullOrEmpty(ticketNumber)) {
      throw new SupercashInvalidValueException("Ticket ID must be provided.");
    }

    TicketRequest request = new TicketRequest();
    request.setIdGaragem(properties.getParkingLotId());
    request.setNumeroTicket(ticketNumber);
    request.setUdid(userId);

// This logic is great, but is could be allow a client to set another sale_id
//    if (saleId.isPresent()) {
//      Long sid = saleId.get();
//      if (sid < 0) {
//        saleId = Optional.of(Long.valueOf(properties.getSaleId().longValue()));
//      }
//      // Paid tickets will resolve an an error
//      request.setIdPromocao(saleId.get());
//    } else {
//      request.setIdPromocao(Long.valueOf(properties.getSaleId().longValue()));
//    }
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

      ticketStatus = parkingTicketPaymentsApi.getTicketUsingPOST(apiKey, request,
              properties.getApiKeyId());

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

    } catch (JsonProcessingException jsonError) {
      LOG.error("Error deserializing status ticket to json.", jsonError);
    }

    long allowedExitEpoch = ticketStatus.getDataPermitidaSaida();
    Long allowedExitEpochAfterLastPaymentObj = ticketStatus.getDataPermitidaSaidaUltimoPagamento();
    if (allowedExitEpochAfterLastPaymentObj != null) {
      if (allowedExitEpochAfterLastPaymentObj > allowedExitEpoch) {
        allowedExitEpoch = allowedExitEpochAfterLastPaymentObj;
        ticketStatus.setDataPermitidaSaida(allowedExitEpoch);
      }
    }

    if (!validate) {
      return new ParkingTicketStatus(ticketStatus);
    }

    int ticketFee = ticketStatus.getTarifa();
    int ticketFeePaid = ticketStatus.getTarifaPaga();
    String message = "";
    SupercashTicketStatus supercashTicketStatus = SupercashTicketStatus.NOT_PAID;

    long queryEpoch = ticketStatus.getDataConsulta();
    long entryEpoch = ticketStatus.getDataDeEntrada();

    LocalDateTime queryDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(queryEpoch), TimeZone.getDefault()
            .toZoneId());
    LocalDateTime allowedExitDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(allowedExitEpoch),
            TimeZone.getDefault().toZoneId());

    if (ticketFee == 0) {
      if (allowedExitEpoch - entryEpoch < 0) {
        message += "Today is free.";
        supercashTicketStatus = SupercashTicketStatus.FREE;
      } else {
        if (queryEpoch - entryEpoch <= properties.getGracePeriod() * 1000) {
          message += "You can leave the parking lot until " + allowedExitDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + ".";
          supercashTicketStatus = SupercashTicketStatus.GRACE_PERIOD;
        }
      }
      LOG.debug(message);
      SupercashAmountIsZeroException exception = new SupercashAmountIsZeroException(message);
      exception.addField("entry_date", entryEpoch);
      exception.addField("exit_allowed_date", allowedExitEpoch);
      if (throwExceptionWhileValidating) throw exception;

    } else {
      if (ticketFeePaid >= ticketFee && queryDateTime.isBefore(allowedExitDateTime)) {
        message = "The ticket is already paid.";
        LOG.debug(message);
        supercashTicketStatus = SupercashTicketStatus.PAID;
        if (throwExceptionWhileValidating) throw new SupercashTransactionAlreadyPaidException(message);

      } else {
        if (amount != ticketFee) {
          supercashTicketStatus = SupercashTicketStatus.NOT_PAID;
          if (throwExceptionWhileValidating) {
            message = "Amount has to be equal to ticket fee. Amount provided is " + amount + " and Ticket fee is " +
                    ticketFee;
            LOG.debug(message);
            throw new SupercashInvalidValueException(message);
          }
        }
      }
    }

    return new ParkingTicketStatus(ticketStatus, supercashTicketStatus);
  }

}
