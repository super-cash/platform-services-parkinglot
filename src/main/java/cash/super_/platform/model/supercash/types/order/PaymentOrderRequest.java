package cash.super_.platform.model.supercash.types.order;

import cash.super_.platform.model.supercash.types.charge.PaymentChargeRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashSet;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentOrderRequest extends PaymentOrder {

    @JsonProperty(value = "charges")
    private Set<PaymentChargeRequest> chargeRequests = new HashSet<>();

    public Set<PaymentChargeRequest> getChargeRequests() {
        return chargeRequests;
    }

    public void setChargeRequests(Set<PaymentChargeRequest> chargeRequests) {
        this.chargeRequests = chargeRequests;
    }

    public void addChargeRequest(PaymentChargeRequest chargeRequest) {
        this.chargeRequests.add(chargeRequest);
    }

    public void clearChargeRequest() {
        this.chargeRequests.clear();
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(System.lineSeparator());
        sb.append("PaymentOrderRequest{");
        sb.append(System.lineSeparator());
        sb.append("  \"chargeRequests\": ").append(chargeRequests).append(',').append(System.lineSeparator());
        sb.append(super.toString()).append(System.lineSeparator()).append('}');
        return sb.toString();
    }
}
