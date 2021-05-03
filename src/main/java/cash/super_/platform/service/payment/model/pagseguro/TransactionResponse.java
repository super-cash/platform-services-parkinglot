package cash.super_.platform.service.payment.model.pagseguro;

import cash.super_.platform.service.payment.model.TransactionResponseSummary;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionResponse {

    @JsonProperty(value = "order_response")
    private OrderResponse orderResponse;

    @JsonProperty(value = "charge_response")
    private ChargeResponse chargeResponse;

    public OrderResponse getOrderResponse() {
        return orderResponse;
    }

    public void setOrderResponse(OrderResponse orderResponse) {
        this.orderResponse = orderResponse;
    }

    public ChargeResponse getChargeResponse() {
        return chargeResponse;
    }

    public void setChargeResponse(ChargeResponse chargeResponse) {
        this.chargeResponse = chargeResponse;
    }

    public TransactionResponseSummary summary() {
        TransactionResponseSummary summary = new TransactionResponseSummary();
        if (this.orderResponse != null) {
            ChargeResponse cr = this.orderResponse.getChargeResponses().get(0);
            summary.setTransactionId(this.orderResponse.getId());
            summary.setPaidAmount(cr.getAmount().getSummary().getPaid());
            summary.setMetadata(cr.getMetadata());
            summary.setStatus(chargeResponse.getStatus().toPagarmeStatus());
//            summary.setGatewayTransactionId();
//            summary.setUuid();
        }
        return summary;
    }
}
