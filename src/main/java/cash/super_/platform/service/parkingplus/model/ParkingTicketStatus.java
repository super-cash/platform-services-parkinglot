package cash.super_.platform.service.parkingplus.model;

import cash.super_.platform.client.parkingplus.model.RetornoConsulta;

/**
 * The status of parking tickets wrapped.
 *
 * @author marcellodesales
 *
 */
public class ParkingTicketStatus {

  private RetornoConsulta status;

  // Used for deserialization
  public ParkingTicketStatus() {
    
  }

  public ParkingTicketStatus(RetornoConsulta ticketStatus) {
    this.status = ticketStatus;
  }

  public RetornoConsulta getStatus() {
    return status;
  }

  public void setStatus(RetornoConsulta ticketStatus) {
    this.status = ticketStatus;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + " [status=" + status + "]";
  }
}
