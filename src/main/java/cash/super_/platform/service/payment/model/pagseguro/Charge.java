package cash.super_.platform.service.payment.model.pagseguro;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class Charge {

    @JsonProperty(value = "reference_id")
    @Length(max = 64)
    private String referenceId;

    @Length(max = 64)
    private String description;

    private Amount amount;

    @JsonProperty(value = "notification_urls")
    private List<String> notificationUrls = new ArrayList<>();

    private Map<String, String> metadata = new HashMap<>();

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
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

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public void addMetadata(String key, String value) {
        this.metadata.put(key, value);
    }

    @Override
    public String toString() {
        return "Charge{" +
                "referenceId='" + referenceId + '\'' +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", notificationUrls=" + notificationUrls +
                ", metadata=" + metadata +
                '}';
    }
}
