package cash.super_.platform.service.payment.model.supercash;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentOrderRequest extends PaymentOrder {

    @JsonProperty(value = "charges")
    private List<PaymentChargeRequest> chargeRequests = new ArrayList<>();

    public List<PaymentChargeRequest> getChargeRequests() {
        return chargeRequests;
    }

    public void setChargeRequests(List<PaymentChargeRequest> chargeRequests) {
        this.chargeRequests = chargeRequests;
    }

    public void addChargeRequest(PaymentChargeRequest chargeRequest) {
        this.chargeRequests.add(chargeRequest);
    }

    public void clearChargeRequest() {
        this.chargeRequests.clear();
    }
}
