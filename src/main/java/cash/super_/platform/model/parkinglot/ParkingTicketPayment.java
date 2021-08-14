package cash.super_.platform.model.parkinglot;

import cash.super_.platform.client.parkingplus.model.PagamentoAutorizadoRequest;
import cash.super_.platform.client.parkingplus.model.PagamentoRequest;
import cash.super_.platform.model.payment.pagarme.TransactionRequest;
import cash.super_.platform.model.supercash.types.charge.AnonymousPaymentChargeRequest;

/**
 * The ticket payment body.
 *
 * @author marcellodesales
 * @author leandromsales
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

  /**
   * Process the payment with Supercash Gateway with few payment information (e.g only creditcard info)
   */
  private AnonymousPaymentChargeRequest anonymousTicketPaymentRequest;

  // Used for deserialization
  public ParkingTicketPayment() {
    
  }

  public ParkingTicketPayment(PagamentoAutorizadoRequest request) {
    this.authorizedRequest = request;
  }

  public ParkingTicketPayment(PagamentoRequest request) {
    this.request = request;
  }

  public ParkingTicketPayment(TransactionRequest payTicketRequest) {
    this.payTicketRequest = payTicketRequest; }

  public ParkingTicketPayment(AnonymousPaymentChargeRequest anonymousTicketPaymentRequest) {
    this.anonymousTicketPaymentRequest = anonymousTicketPaymentRequest;
  }

  public PagamentoAutorizadoRequest getAuthorizedRequest() {
    return authorizedRequest;
  }

  public PagamentoRequest getRequest() {
    return request;
  }

  public TransactionRequest getPayTicketRequest() { return payTicketRequest; }

  public AnonymousPaymentChargeRequest getAnonymousTicketPaymentRequest() {
    return anonymousTicketPaymentRequest;
  }

  @Override
  public String toString() {
    return "ParkingTicketPayment{" +
            "authorizedRequest=" + authorizedRequest +
            ", request=" + request +
            ", transactionRequest=" + payTicketRequest +
            ", anonymousTicketPaymentRequest=" + anonymousTicketPaymentRequest +
            '}';
  }
}
