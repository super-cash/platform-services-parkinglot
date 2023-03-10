package cash.super_.platform.model.supercash.types.order;

import cash.super_.platform.model.supercash.link.Link;
import cash.super_.platform.model.supercash.types.charge.PaymentChargeResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentOrderResponse extends PaymentOrder {

    @JsonProperty(value = "created_at")
    private String createdAt;;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @JoinColumn(name = "payment_id", foreignKey = @ForeignKey(name = "payment_order_payment_qrcode_fk"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonProperty(value = "qr_codes")
    private Set<OrderQRCode> qrCodes;

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinColumn(name = "payment_order_id", foreignKey = @ForeignKey(name = "payment_order_link_fk"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Link> links;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, mappedBy = "payment", orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonProperty(value = "charges")
    private Set<PaymentChargeResponse> charges = new HashSet<>();

    public Set<PaymentChargeResponse> getCharges() {
        return charges;
    }

    public void setCharges(Set<PaymentChargeResponse> charges) {
        this.charges = charges;
    }

    public void addCharge(PaymentChargeResponse charge) {
        this.charges.add(charge);
    }

    public void clearCharge() {
        this.charges.clear();
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Set<OrderQRCode> getQrCodes() {
        return qrCodes;
    }

    public void setQrCodes(Set<OrderQRCode> qrCodes) {
        this.qrCodes = qrCodes;
    }

    public Set<Link> getLinks() {
        return links;
    }

    public void setLinks(Set<Link> links) {
        this.links = links;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(System.lineSeparator());
        sb.append("PaymentOrderResponse{");
        sb.append(System.lineSeparator());
        sb.append("  \"createdAt\": \"").append(createdAt).append("\",").append(System.lineSeparator());
        sb.append("  \"qrCodes\": ").append(qrCodes).append(',').append(System.lineSeparator());
        sb.append("  \"links\": ").append(links).append(',').append(System.lineSeparator());
        sb.append("  \"charges\": ").append(charges).append(',').append(System.lineSeparator());
        sb.append('}').append(System.lineSeparator());
        sb.append(super.toString()).append(System.lineSeparator());
        return sb.toString();
    }
}
