package cash.super_.platform.service.payment.model.supercash;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity(name = "payment_charge_response")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentChargeResponse extends PaymentCharge {

    /**
     * Supercash internal ID, defined when a request enter for payment.
     */
    protected UUID uuid;

    @Length(max = 41)
    private String chargeId;

    @JsonProperty(required = true)
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

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonProperty(value = "payment_response")
    private ChargePaymentResponse paymentResponse;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(name = "payment_payment_link",
               joinColumns = @JoinColumn(name = "payment_id", foreignKey = @ForeignKey(name =
                       "payment_payment_payment_link_fk")),
               inverseJoinColumns = @JoinColumn(name = "link_id", foreignKey = @ForeignKey(name =
                       "payment_link_payment_payment_link_fk")))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Link> links = new HashSet<>();

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @JoinColumn(foreignKey = @ForeignKey(name = "payment_charge_response_payment_method_response_fk"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonProperty(value = "payment_method")
    private ChargePaymentMethodResponse paymentMethod;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "payment_id", foreignKey =
        @ForeignKey(name = "payment_order_response_payment_charge_response_fk"))
    private PaymentOrderResponse payment;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getChargeId() {
        return chargeId;
    }

    public void setChargeId(String chargeId) {
        this.chargeId = chargeId;
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

    public ChargePaymentResponse getPaymentResponse() {
        return paymentResponse;
    }

    public void setPaymentResponse(ChargePaymentResponse chargePaymentResponse) {
        this.paymentResponse = chargePaymentResponse;
    }

    public Set<Link> getLinks() {
        return links;
    }

    public void setLinks(Set<Link> links) {
        this.links = links;
    }

    public ChargePaymentMethodResponse getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(ChargePaymentMethodResponse paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentOrderResponse getPayment() {
        return payment;
    }

    public void setPayment(PaymentOrderResponse payment) {
        this.payment = payment;
    }

    @Override
    public String toString() {
        return "PaymentChargeResponse{" +
                "metadata=" + metadata +
                ", uuid=" + uuid +
                ", chargeId='" + chargeId + '\'' +
                ", status=" + status +
                ", createdAt='" + createdAt + '\'' +
                ", paidAt='" + paidAt + '\'' +
                ", paymentResponse=" + paymentResponse +
                ", links=" + links +
                ", paymentMethod=" + paymentMethod +
                "} " + super.toString();
    }
}
