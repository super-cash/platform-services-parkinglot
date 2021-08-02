package cash.super_.platform.service.parkinglot.model;

import cash.super_.platform.client.parkingplus.model.RetornoConsulta;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.TimeZone;

/**
 * The status of parking tickets wrapped.
 *
 * @author marcellodesales
 * @author leandromsales
 *
 */
public class ParkingTicketStatus {

  /**
   * The interpretation of what the ticket state is for supercash based on the status of the ticket with WPS.
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private ParkingTicketState state;
  /**
   * The interpretation of the grace period max time for supercash based on the status of the ticket.
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private long gracePeriodMaxTime;
  /**
   * The status of the ticket according to WPS
   */
  private RetornoConsulta status;

  /**
   * Constructs a new status. However, it's only used for deserialization.
   */
  public ParkingTicketStatus() {

  }

  /**
   * Makes a new instance based on the result of WPS and the ticket state
   * @param status the status of the ticket in WPS
   * @param state the state based on the state of the ticket
   */
  public ParkingTicketStatus(RetornoConsulta status, ParkingTicketState state, long gracePeriodMaxTime) {
    this.status = status;
    this.state = state;
    this.gracePeriodMaxTime = gracePeriodMaxTime;
  }

  public RetornoConsulta getStatus() {
    return status;
  }

  public void setStatus(RetornoConsulta status) {
    this.status = status;
  }

  public ParkingTicketState getState() {
    return state;
  }

  public void setState(ParkingTicketState state) {
    this.state = state;
  }

  public long getGracePeriodMaxTime() {
    return this.gracePeriodMaxTime;
  }

  public void setGracePeriodMaxTime(long gracePeriodMaxTime) {
    this.gracePeriodMaxTime = gracePeriodMaxTime;
  }

  public static LocalDateTime calculateGracePeriod(RetornoConsulta queryResult, int gracePeriodMinutes, String zoneId) {
    long entryEpoch = queryResult.getDataDeEntrada();
    LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(entryEpoch), TimeZone.getDefault().toZoneId());

    // For the calls for WPS
    if ("America/Sao_Paulo".equals(zoneId)) {
      return dateTime.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.of("America/Sao_Paulo")).toLocalDateTime().plusMinutes(gracePeriodMinutes);
    }

    // For the calls for Testing (local time
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(entryEpoch), TimeZone.getDefault().toZoneId()).plusMinutes(gracePeriodMinutes);
  }

  public static long calculateAllowedExitDateTime(RetornoConsulta queryResult) {
    // Adjust the exit times depending on the payment
    long allowedExitEpoch = queryResult.getDataPermitidaSaida();
    Long allowedExitEpochAfterLastPaymentObj = queryResult.getDataPermitidaSaidaUltimoPagamento();
    if (allowedExitEpochAfterLastPaymentObj != null) {
      if (allowedExitEpochAfterLastPaymentObj > allowedExitEpoch) {
        allowedExitEpoch = allowedExitEpochAfterLastPaymentObj;
        queryResult.setDataPermitidaSaida(allowedExitEpoch);
      }
    }
    return allowedExitEpoch;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + "{" +
            "ticketState=" + state +
            ", status=" + status +
            '}';
  }
}
