package cash.super_.platform.service.payment.model.supercash;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentResponse {

    @JsonProperty(value = "order_response")
    private PaymentOrderResponse orderResponse;

    @JsonProperty(value = "charge_response")
    private PaymentChargeResponse chargeResponse;

    public PaymentOrderResponse getOrderResponse() {
        return orderResponse;
    }

    public void setOrderResponse(PaymentOrderResponse orderResponse) {
        this.orderResponse = orderResponse;
    }

    public PaymentChargeResponse getChargeResponse() {
        return chargeResponse;
    }

    public void setChargeResponse(PaymentChargeResponse chargeResponse) {
        this.chargeResponse = chargeResponse;
    }

    @Override
    public String toString() {
        return "TransactionResponse{" +
                "orderResponse=" + orderResponse +
                ", chargeResponse=" + chargeResponse +
                '}';
    }
}
