package cash.super_.platform.service.payment.model.supercash;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "payment")
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonProperty(value = "reference_id")
    @Length(max = 64)
    private String referenceId;

    private PaymentGateway gateway = PaymentGateway.PAGSEGURO;

    @JsonProperty(value = "notification_urls")
    @Transient
    private List<String> notificationUrls = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public PaymentGateway getGateway() {
        return gateway;
    }

    public void setGateway(PaymentGateway gateway) {
        this.gateway = gateway;
    }

    public List<String> getNotificationUrls() {
        return notificationUrls;
    }

    public void setNotificationUrls(List<String> notificationUrls) {
        this.notificationUrls = notificationUrls;
    }

    public void addNotificationUrl(String url) {
        this.notificationUrls.add(url);
    }

    public void clearNotificationUrls() {
        this.notificationUrls = new ArrayList<>();
    }

    public PaymentResponseSummary summary() {
        PaymentResponseSummary summary = new PaymentResponseSummary();
        PaymentChargeResponse chargeResponse;
        if (this instanceof PaymentOrderResponse) {
            PaymentOrderResponse orderResponse = (PaymentOrderResponse) this;
            chargeResponse = orderResponse.getCharges().stream().findFirst().get();
        } else {
            chargeResponse = (PaymentChargeResponse) this;
        }
        summary.setTransactionId(this.getId());
        summary.setPaidAmount(chargeResponse.getAmount().getSummary().getPaid());
        summary.setMetadata(chargeResponse.getMetadata());
        summary.setStatus(chargeResponse.getStatus());
        summary.setGateway(chargeResponse.getGateway());
        String uuid = chargeResponse.getMetadata().get("uuid");
        if (uuid != null) {
            summary.setUuid(uuid);
        }
        return summary;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(System.lineSeparator());
        sb.append("Payment{");
        sb.append(System.lineSeparator());
        sb.append("  \"id\": ").append(id).append(',').append(System.lineSeparator());
        sb.append("  \"referenceId\": \"").append(referenceId).append("\",").append(System.lineSeparator());
        sb.append("  \"gateway\": ").append(gateway).append(',').append(System.lineSeparator());
        sb.append("  \"notificationUrls\": ").append(notificationUrls).append(',').append(System.lineSeparator());
        sb.append('}').append(System.lineSeparator());
        return sb.toString();
    }

    public enum PaymentRequestType {
        CHARGE, ORDER;
    }

    public enum PaymentGateway {
        PAGSEGURO, PAGARME, STRIPE, EBANX, REDE, MULTIPAG;
    }
}
