package cash.super_.platform.service.parkingplus.model;

import cash.super_.platform.client.parkingplus.model.PagamentoAutorizadoRequest;
import cash.super_.platform.client.parkingplus.model.PagamentoRequest;

/**
 * The ticket payment body.
 *
 * @author marcellodesales
 *
 */
public class ParkingTicketPayment {

  /**
   * When the payment is done via the Gateway and it does NOT take CC info
   */
  private PagamentoAutorizadoRequest authorizedRequest;
  /**
   * When the payment is done via the Parking Plus' Gateway itself, requiring CC info
   */
  private PagamentoRequest request;

  // Used for deserialization
  public ParkingTicketPayment() {
    
  }

  public ParkingTicketPayment(PagamentoAutorizadoRequest authorizedRequest) {
    this.authorizedRequest = authorizedRequest;
  }

  public ParkingTicketPayment(PagamentoRequest request) {
    this.request = request;
  }

  public PagamentoAutorizadoRequest getAuthorizedRequest() {
    return authorizedRequest;
  }

  public PagamentoRequest getRequest() {
    return request;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + " [request=" + request + "]";
  }
}
