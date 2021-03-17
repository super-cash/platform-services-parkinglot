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

  public ParkingTicketStatus(RetornoConsulta status) {
    this.status = status;
  }

  public RetornoConsulta getStatus() {
    return status;
  }

  public void setStatus(RetornoConsulta status) {
    this.status = status;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + " [status=" + status + "]";
  }
}
