package cash.super_.platform.model.supercash;

import cash.super_.platform.model.supercash.types.charge.AnonymousPaymentChargeRequest;
import cash.super_.platform.model.supercash.types.charge.PaymentChargeRequest;
import cash.super_.platform.model.supercash.types.order.PaymentOrderRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentAnyRequest {

    @JsonProperty(value = "order_request")
    private PaymentOrderRequest orderRequest;

    @JsonProperty(value = "charge_request")
    private PaymentChargeRequest chargeRequest;

    @JsonProperty(value = "anonymousTicketPaymentRequest")
    private AnonymousPaymentChargeRequest anonymousTicketPaymentRequest;

    public PaymentAnyRequest() {
        
    }

    public PaymentAnyRequest(PaymentOrderRequest orderRequest) {
        this.orderRequest = orderRequest;
    }

    public PaymentAnyRequest(PaymentChargeRequest chargeRequest) {
        this.chargeRequest = chargeRequest;
    }
    
    public PaymentOrderRequest getOrderRequest() {
        return orderRequest;
    }

    public void setOrderRequest(PaymentOrderRequest orderRequest) {
        this.orderRequest = orderRequest;
    }

    public PaymentChargeRequest getChargeRequest() {
        return chargeRequest;
    }

    public void setChargeRequest(PaymentChargeRequest chargeRequest) {
        this.chargeRequest = chargeRequest;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(System.lineSeparator());
        sb.append("PaymentAnyRequest{");
        sb.append(System.lineSeparator());
        sb.append("  \"orderRequest\": ").append(orderRequest).append(',').append(System.lineSeparator());
        sb.append("  \"chargeRequest\": ").append(chargeRequest).append(',').append(System.lineSeparator());
        sb.append("  \"anonymousRequest\": ").append(anonymousTicketPaymentRequest).append(',').append(System.lineSeparator());
        sb.append(super.toString()).append(System.lineSeparator()).append('}');
        return sb.toString();
    }
}
