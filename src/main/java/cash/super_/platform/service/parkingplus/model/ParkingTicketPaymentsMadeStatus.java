package cash.super_.platform.service.parkingplus.model;

import java.util.List;
import cash.super_.platform.client.parkingplus.model.PagamentoEfetuado;

/**
 * The status of parking ticket payments made
 *
 * @author marcellodesales
 *
 */
public class ParkingTicketPaymentsMadeStatus {

  private List<PagamentoEfetuado> status;

  // Used for deserialization
  public ParkingTicketPaymentsMadeStatus() {
    
  }

  public ParkingTicketPaymentsMadeStatus(List<PagamentoEfetuado> paymentsMade) {
    this.status = paymentsMade;
  }

  public List<PagamentoEfetuado> getStatus() {
    return status;
  }

  public void setStatus(List<PagamentoEfetuado> status) {
    this.status = status;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + " [status=" + status + "]";
  }
}
