package cash.super_.platform.service.parkinglot.model;

import java.util.List;
import cash.super_.platform.client.parkingplus.model.PagamentoEfetuado;

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
