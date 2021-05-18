package cash.super_.platform.service.payment.model.supercash.types.charge;

import cash.super_.platform.service.payment.model.supercash.link.Link;
import cash.super_.platform.service.payment.model.supercash.types.order.PaymentOrderResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentChargeResponse extends PaymentCharge {

    /**
     * Supercash internal ID, defined when a request enter for payment.
     */
    protected UUID uuid;

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
    @JoinColumn(foreignKey = @ForeignKey(name = "payment_charge_payment_charge_response_fk"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonProperty(value = "payment_response")
    private ChargePaymentResponse paymentResponse;

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinColumn(name = "payment_charge_id", foreignKey = @ForeignKey(name = "payment_charge_link_fk"))
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
        final StringBuffer sb = new StringBuffer(System.lineSeparator());
        sb.append("PaymentChargeResponse{");
        sb.append(System.lineSeparator());
        sb.append("  \"uuid\": ").append(uuid).append(',').append(System.lineSeparator());
        sb.append("  \"status\": ").append(status).append(',').append(System.lineSeparator());
        sb.append("  \"createdAt\": \"").append(createdAt).append("\",").append(System.lineSeparator());
        sb.append("  \"paidAt\": \"").append(paidAt).append("\",").append(System.lineSeparator());
        sb.append("  \"paymentResponse\": ").append(paymentResponse).append(',').append(System.lineSeparator());
        sb.append("  \"links\": ").append(links).append(',').append(System.lineSeparator());
        sb.append("  \"paymentMethod\": ").append(paymentMethod).append(',').append(System.lineSeparator());
        sb.append(super.toString()).append(System.lineSeparator()).append('}');
        return sb.toString();
    }
}
