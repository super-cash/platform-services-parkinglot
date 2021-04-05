package cash.super_.platform.service.pagarme.transactions.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Objects;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "pagarme_split_rule")
public class SplitRule {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
        return "SplitRule{" +
                "recipientId='" + recipientId + '\'' +
                ", liable=" + liable +
                ", chargeProcessingFee=" + chargeProcessingFee +
                ", percentage='" + percentage + '\'' +
                ", amount='" + amount + '\'' +
                ", chargeRemainderFee=" + chargeRemainderFee +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SplitRule)) return false;
        SplitRule splitRule = (SplitRule) o;
        return getRecipientId().equals(splitRule.getRecipientId()) && getLiable().equals(splitRule.getLiable()) && getChargeProcessingFee().equals(splitRule.getChargeProcessingFee()) && getPercentage().equals(splitRule.getPercentage()) && getAmount().equals(splitRule.getAmount()) && getChargeRemainderFee().equals(splitRule.getChargeRemainderFee());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRecipientId(), getLiable(), getChargeProcessingFee(), getPercentage(), getAmount(), getChargeRemainderFee());
    }
}
