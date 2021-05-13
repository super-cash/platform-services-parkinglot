package cash.super_.platform.service.payment.model.supercash;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentAnyRequest {

    @JsonProperty(value = "order_request")
    private PaymentOrderRequest orderRequest;

    @JsonProperty(value = "charge_request")
    private PaymentChargeRequest chargeRequest;

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
        return "PaymentRequest{" +
                "orderRequest=" + orderRequest +
                ", chargeRequest=" + chargeRequest +
                '}';
    }
}
