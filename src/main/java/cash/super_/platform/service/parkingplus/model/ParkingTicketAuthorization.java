package cash.super_.platform.service.parkingplus.model;

import cash.super_.platform.client.parkingplus.model.PagamentoAutorizadoRequest;

/**
 * The status of parking tickets wrapped.
 *
 * @author marcellodesales
 *
 */
public class ParkingTicketAuthorization {

  private PagamentoAutorizadoRequest request;

  // Used for deserialization
  public ParkingTicketAuthorization() {
    
  }

  public ParkingTicketAuthorization(PagamentoAutorizadoRequest paymentAuthRequest) {
    this.request = paymentAuthRequest;
  }

  public PagamentoAutorizadoRequest getRequest() {
    return request;
  }

  public void setStatus(PagamentoAutorizadoRequest authorizedPayment) {
    this.request = authorizedPayment;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + " [request=" + request + "]";
  }
}
