package cash.super_.platform.model.parkinglot;

import cash.super_.platform.client.parkingplus.model.RetornoConsulta;
import cash.super_.platform.util.DateTimeUtil;
import com.fasterxml.jackson.annotation.JsonInclude;

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

    // Convert to the Brazil timeframe
    if (status.getDataConsulta() != null && status.getDataConsulta() > 0) {
      status.setDataConsulta(DateTimeUtil.convertToTimezone(status.getDataConsulta()));
    }
    if (status.getDataPermitidaSaida() != null && status.getDataPermitidaSaida() > 0) {
      status.setDataPermitidaSaida(DateTimeUtil.convertToTimezone(status.getDataPermitidaSaida()));
    }
    if (status.getDataDeEntrada() != null && status.getDataDeEntrada() > 0) {
      status.setDataDeEntrada(DateTimeUtil.convertToTimezone(status.getDataDeEntrada()));
    }
    if (status.getDataPermitidaSaidaUltimoPagamento() != null && status.getDataPermitidaSaidaUltimoPagamento() > 0) {
      status.setDataPermitidaSaidaUltimoPagamento(DateTimeUtil.convertToTimezone(status.getDataPermitidaSaidaUltimoPagamento()));
    }
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

  public static long calculateAllowedExitDateTime(RetornoConsulta queryResult) {
    // Adjust the exit times depending on the payment
    if (queryResult.getDataPermitidaSaida() == null) {
      return DateTimeUtil.getNow();
    }
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

  public boolean canBePaid() {
    return ParkingTicketState.NOT_PAID == this.getState();
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + "{" +
            "ticketState=" + state +
            ", status=" + status +
            '}';
  }
}
