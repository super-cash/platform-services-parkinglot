package cash.super_.platform.model.supercash;

import cash.super_.platform.model.supercash.types.charge.PaymentChargeResponse;
import cash.super_.platform.model.supercash.types.order.PaymentOrderResponse;
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
        final StringBuffer sb = new StringBuffer(System.lineSeparator());
        sb.append("PaymentResponse{");
        sb.append(System.lineSeparator());
        sb.append("  \"orderResponse\": ").append(orderResponse).append(',').append(System.lineSeparator());
        sb.append("  \"chargeResponse\": ").append(chargeResponse).append(',').append(System.lineSeparator());
        sb.append(super.toString()).append(System.lineSeparator()).append('}');
        return sb.toString();
    }
}
