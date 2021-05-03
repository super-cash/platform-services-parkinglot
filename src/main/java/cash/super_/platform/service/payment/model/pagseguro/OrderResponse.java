package cash.super_.platform.service.payment.model.pagseguro;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponse extends Order {

    @Length(max = 64)
    public String id;

    @JsonProperty(value = "created_at")
    private String createdAt;;

    @JsonProperty(value = "qr_codes")
    private List<QRCode> qrCodes;

    private List<Link> links;

    @JsonProperty(value = "charges")
    private List<ChargeResponse> chargeResponses = new ArrayList<>();

    public List<ChargeResponse> getChargeResponses() {
        return chargeResponses;
    }

    public void setChargeResponses(List<ChargeResponse> chargeResponses) {
        this.chargeResponses = this.chargeResponses;
    }

    public void addChargeResponse(ChargeResponse chargeResponse) {
        this.chargeResponses.add(chargeResponse);
    }

    public void clearChargeResponses() {
        this.chargeResponses.clear();
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<QRCode> getQrCodes() {
        return qrCodes;
    }

    public void setQrCodes(List<QRCode> qrCodes) {
        this.qrCodes = qrCodes;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    @Override
    public String toString() {
        return "OrderResponse{" +
                "id='" + id + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", qrCodes=" + qrCodes +
                ", links=" + links +
                "} " + super.toString();
    }
}
