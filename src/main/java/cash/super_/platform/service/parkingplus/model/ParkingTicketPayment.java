package cash.super_.platform.service.parkingplus.model;

import cash.super_.platform.client.parkingplus.model.PagamentoAutorizadoRequest;
import cash.super_.platform.client.parkingplus.model.PagamentoRequest;
import cash.super_.platform.service.pagarme.transactions.models.TransactionRequest;

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

  /**
   * Process the payment with Supercash Gateway
   */
  private TransactionRequest payTicketRequest;

  // Used for deserialization
  public ParkingTicketPayment() {
    
  }

  public ParkingTicketPayment(PagamentoAutorizadoRequest request) {
    this.authorizedRequest = request;
  }

  public ParkingTicketPayment(PagamentoRequest request) {
    this.request = request;
  }

  public ParkingTicketPayment(TransactionRequest request) {
    this.payTicketRequest = request; }

  public PagamentoAutorizadoRequest getAuthorizedRequest() {
    return authorizedRequest;
  }

  public PagamentoRequest getRequest() {
    return request;
  }

  public TransactionRequest getPayTicketRequest() { return payTicketRequest; }

  @Override
  public String toString() {
    return "ParkingTicketPayment{" +
            "authorizedRequest=" + authorizedRequest +
            ", request=" + request +
            ", transactionRequest=" + payTicketRequest +
            '}';
  }
}
