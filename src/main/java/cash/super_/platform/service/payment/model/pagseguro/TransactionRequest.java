package cash.super_.platform.service.payment.model.pagseguro;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionRequest {

    @JsonProperty(value = "order_request")
    private OrderRequest orderRequest;

    @JsonProperty(value = "charge_request")
    private ChargeRequest chargeRequest;

    public TransactionRequest() {
        
    }

    public TransactionRequest(OrderRequest orderRequest) {
        this.orderRequest = orderRequest;
    }

    public TransactionRequest(ChargeRequest chargeRequest) {
        this.chargeRequest = chargeRequest;
    }
    
    public OrderRequest getOrderRequest() {
        return orderRequest;
    }

    public void setOrderRequest(OrderRequest orderRequest) {
        this.orderRequest = orderRequest;
    }

    public ChargeRequest getChargeRequest() {
        return chargeRequest;
    }

    public void setChargeRequest(ChargeRequest chargeRequest) {
        this.chargeRequest = chargeRequest;
    }

}
