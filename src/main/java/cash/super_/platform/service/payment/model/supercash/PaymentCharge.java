package cash.super_.platform.service.payment.model.supercash;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Entity(name = "payment_charge")
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class PaymentCharge extends Payment {

    @Length(max = 64)
    private String description = "";

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @JoinColumn(foreignKey = @ForeignKey(name = "payment_charge_payment_amount_fk"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Amount amount;

    @ElementCollection
    @CollectionTable(name="payment_metadata", joinColumns = @JoinColumn(name = "payment_id", foreignKey =
        @ForeignKey(name = "payment_charge_metadata_fk")))
    @MapKeyColumn(name="name")
    @Column(name="value")
    @JoinColumn(name="payment_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    protected Map<String, String> metadata = new HashMap<>();

    @JsonProperty(value = "split_rules")
    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @JoinColumn(name = "payment_id", foreignKey = @ForeignKey(name = "payment_charge_payment_split_rule_fk"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<PaymentSplitRule> splitRules;

    public PaymentCharge() {
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

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public void addMetadata(String key, String value) {
        this.metadata.put(key, value);
    }

    public Set<PaymentSplitRule> getSplitRules() {
        return splitRules;
    }

    public void setSplitRules(Set<PaymentSplitRule> paymentSplitRules) {
        this.splitRules = paymentSplitRules;
    }

    public void addSplitRule(PaymentSplitRule paymentSplitRule) {
        this.splitRules.add(paymentSplitRule);
    }

    public void clearSplitRule() {
        this.splitRules.clear();
    }

    @Override
    public String toString() {
        return "Charge{" +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", metadata=" + metadata +
                '}';
    }
}
