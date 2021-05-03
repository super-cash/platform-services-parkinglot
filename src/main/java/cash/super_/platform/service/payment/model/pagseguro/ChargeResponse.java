package cash.super_.platform.service.payment.model.pagseguro;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;

import java.util.List;

public class ChargeResponse extends Charge {

    @Length(max = 41)
    private String id;

    private ChargeStatus status;

//    @JsonProperty(value = "created_at")
//    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss-ZZZZZ")
//    private LocalDateTime createdAt;
//
//    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss-ZZZZZ")
//    @JsonProperty(value = "paid_at")
//    private LocalDateTime paidAt;

    @JsonProperty(value = "created_at")
    private String createdAt;

    @JsonProperty(value = "paid_at")
    private String paidAt;

    @JsonProperty(value = "payment_response")
    private PaymentResponse paymentResponse;

    private List<Link> links;

    @JsonProperty(value = "payment_method")
    private PaymentMethodResponse paymentMethod;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ChargeStatus getStatus() {
        return status;
    }

    public void setStatus(ChargeStatus status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(String paidAt) {
        this.paidAt = paidAt;
    }

    public PaymentResponse getPaymentResponse() {
        return paymentResponse;
    }

    public void setPaymentResponse(PaymentResponse paymentResponse) {
        this.paymentResponse = paymentResponse;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public PaymentMethodResponse getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethodResponse paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @Override
    public String toString() {
        return "ChargeResponse{" +
                "id='" + id + '\'' +
                ", status='" + status + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", paidAt='" + paidAt + '\'' +
                ", paymentResponse=" + paymentResponse +
                ", links=" + links +
                ", paymentMethod=" + paymentMethod +
                "} " + super.toString();
    }
}
