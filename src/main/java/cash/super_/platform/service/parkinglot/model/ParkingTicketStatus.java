package cash.super_.platform.service.parkinglot.model;

import cash.super_.platform.client.parkingplus.model.RetornoConsulta;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The status of parking tickets wrapped.
 *
 * @author marcellodesales
 * @author leandromsales
 *
 */
public class ParkingTicketStatus {

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonProperty(value = "ticketStatus")
  private SupercashTicketStatus supercashTicketStatus;

  private RetornoConsulta status;

  // Used for deserialization
  public ParkingTicketStatus() {
    
  }

  public ParkingTicketStatus(RetornoConsulta status, SupercashTicketStatus supercashTicketStatus) {
    this.status = status;
    this.supercashTicketStatus = supercashTicketStatus;
  }

  public RetornoConsulta getStatus() {
    return status;
  }

  public void setStatus(RetornoConsulta status) {
    this.status = status;
  }

  public SupercashTicketStatus getSupercashTicketStatus() {
    return supercashTicketStatus;
  }

  public void setSupercashTicketStatus(SupercashTicketStatus supercashTicketStatus) {
    this.supercashTicketStatus = supercashTicketStatus;
  }
//
//  @Override
//  public String toString() {
//    return this.getClass().getSimpleName() +
//            " [status=" + status + "]";
//  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + "{" +
            "supercashTicketStatus=" + supercashTicketStatus +
            ", status=" + status +
            '}';
  }
}
