package cash.super_.platform.model.parkinglot;

import java.util.List;

/**
 * The status of parking ticket payments made
 *
 * @author marcellodesales
 * @author leandromsales
 *
 */
public class ParkingTicketPaymentsMadeStatus {

  private List<ParkingPaidTicketStatus> status;

  // Used for deserialization
  public ParkingTicketPaymentsMadeStatus() {
    
  }

  public ParkingTicketPaymentsMadeStatus(List<ParkingPaidTicketStatus> paymentsMade) {
    this.status = paymentsMade;
  }

  public List<ParkingPaidTicketStatus> getStatus() {
    return status;
  }

  public void setStatus(List<ParkingPaidTicketStatus> status) {
    this.status = status;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + " [status=" + status + "]";
  }
}
