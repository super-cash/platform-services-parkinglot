package cash.super_.platform.service.parkingplus.model;

import cash.super_.platform.client.parkingplus.model.RetornoPagamento;

/**
 * The status of parking tickets wrapped.
 *
 * @author marcellodesales
 *
 */
public class ParkingTicketAuthorizedPaymentStatus {

  private RetornoPagamento status;

  // Used for deserialization
  public ParkingTicketAuthorizedPaymentStatus() {
    
  }

  public ParkingTicketAuthorizedPaymentStatus(RetornoPagamento authorizedPayment) {
    this.status = authorizedPayment;
  }

  public RetornoPagamento getStatus() {
    return status;
  }

  public void setStatus(RetornoPagamento authorizedPayment) {
    this.status = authorizedPayment;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + " [status=" + status + "]";
  }
}
