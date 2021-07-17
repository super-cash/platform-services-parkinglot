package cash.super_.platform.service.parkinglot.model;

import cash.super_.platform.client.parkingplus.model.PagamentoAutorizadoRequest;
import cash.super_.platform.client.parkingplus.model.PagamentoRequest;
import cash.super_.platform.service.payment.model.pagarme.TransactionRequest;
import cash.super_.platform.service.payment.model.supercash.types.charge.PaymentShortChargeRequest;

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
  private PaymentShortChargeRequest shortTicketPaymentRequest;

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

  public ParkingTicketPayment(PaymentShortChargeRequest shortTicketPaymentRequest) {
    this.shortTicketPaymentRequest = shortTicketPaymentRequest;
  }

  public PagamentoAutorizadoRequest getAuthorizedRequest() {
    return authorizedRequest;
  }

  public PagamentoRequest getRequest() {
    return request;
  }

  public TransactionRequest getPayTicketRequest() { return payTicketRequest; }

  public PaymentShortChargeRequest getShortTicketPaymentRequest() {
    return shortTicketPaymentRequest;
  }

  @Override
  public String toString() {
    return "ParkingTicketPayment{" +
            "authorizedRequest=" + authorizedRequest +
            ", request=" + request +
            ", transactionRequest=" + payTicketRequest +
            ", shortTicketPaymentRequest=" + shortTicketPaymentRequest +
            '}';
  }
}
