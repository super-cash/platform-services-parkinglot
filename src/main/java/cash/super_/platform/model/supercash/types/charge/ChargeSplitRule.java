package cash.super_.platform.model.supercash.types.charge;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "payment_split_rule")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChargeSplitRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long splitRuleId;

    @JsonProperty("recipient_id")
    private String recipientId;

    private Boolean liable;

    @JsonProperty("charge_processing_fee")
    private Boolean chargeProcessingFee;

    private Integer percentage;

    private Long amount;

    @JsonProperty("charge_remainder_fee")
    private Boolean chargeRemainderFee;

    public Long getSplitRuleId() {
        return splitRuleId;
    }

    public void setSplitRuleId(Long splitRuleId) {
        this.splitRuleId = splitRuleId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public Boolean getLiable() {
        return liable;
    }

    public void setLiable(Boolean liable) {
        this.liable = liable;
    }

    public Boolean getChargeProcessingFee() {
        return chargeProcessingFee;
    }

    public void setChargeProcessingFee(Boolean chargeProcessingFee) {
        this.chargeProcessingFee = chargeProcessingFee;
    }

    public Integer getPercentage() {
        return percentage;
    }

    public void setPercentage(Integer percentage) {
        this.percentage = percentage;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Boolean getChargeRemainderFee() {
        return chargeRemainderFee;
    }

    public void setChargeRemainderFee(Boolean chargeRemainderFee) {
        this.chargeRemainderFee = chargeRemainderFee;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(System.lineSeparator());
        sb.append("PaymentSplitRule{");
        sb.append(System.lineSeparator());
        sb.append("  \"splitRuleId\": ").append(splitRuleId).append(',').append(System.lineSeparator());
        sb.append("  \"recipientId\": \"").append(recipientId).append("\",").append(System.lineSeparator());
        sb.append("  \"liable\": ").append(liable).append(',').append(System.lineSeparator());
        sb.append("  \"chargeProcessingFee\": ").append(chargeProcessingFee).append(',').append(System.lineSeparator());
        sb.append("  \"percentage\": ").append(percentage).append(',').append(System.lineSeparator());
        sb.append("  \"amount\": ").append(amount).append(',').append(System.lineSeparator());
        sb.append("  \"chargeRemainderFee\": ").append(chargeRemainderFee).append(',').append(System.lineSeparator());
        sb.append(super.toString()).append(System.lineSeparator()).append('}');
        return sb.toString();
    }

}
