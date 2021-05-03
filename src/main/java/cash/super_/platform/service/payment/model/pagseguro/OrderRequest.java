package cash.super_.platform.service.payment.model.pagseguro;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderRequest extends Order {

    @JsonProperty(value = "charges")
    private List<ChargeRequest> chargeRequests = new ArrayList<>();

    public List<ChargeRequest> getChargeRequests() {
        return chargeRequests;
    }

    public void setChargeRequests(List<ChargeRequest> chargeRequests) {
        this.chargeRequests = chargeRequests;
    }

    public void addChargeRequest(ChargeRequest chargeRequest) {
        this.chargeRequests.add(chargeRequest);
    }

    public void clearChargeRequest() {
        this.chargeRequests.clear();
    }

}
