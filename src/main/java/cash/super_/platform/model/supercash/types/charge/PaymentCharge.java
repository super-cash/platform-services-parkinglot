package cash.super_.platform.model.supercash.types.charge;

import cash.super_.platform.model.supercash.Payment;
import cash.super_.platform.model.supercash.amount.Amount;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.*;

@Entity(name = "payment_charge")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class PaymentCharge extends Payment {

    @Length(max = 64)
    private String description = "";

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @JoinColumn(foreignKey = @ForeignKey(name = "payment_charge_payment_amount_fk"))
    @OnDelete(action = OnDeleteAction.CASCADE)
//    @Embedded
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
    private Set<ChargeSplitRule> splitRules;

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

    public Set<ChargeSplitRule> getSplitRules() {
        return splitRules;
    }

    public void setSplitRules(Set<ChargeSplitRule> chargeSplitRules) {
        this.splitRules = chargeSplitRules;
    }

    public void addSplitRule(ChargeSplitRule chargeSplitRule) {
        this.splitRules.add(chargeSplitRule);
    }

    public void clearSplitRule() {
        this.splitRules.clear();
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(System.lineSeparator());
        sb.append("PaymentCharge{");
        sb.append(System.lineSeparator());
        sb.append("  \"description\": \"").append(description).append("\",").append(System.lineSeparator());
        sb.append("  \"amount\": ").append(amount).append(',').append(System.lineSeparator());
        sb.append("  \"metadata\": ").append(metadata).append(',').append(System.lineSeparator());
        sb.append("  \"splitRules\": ").append(splitRules).append(',').append(System.lineSeparator());
        sb.append(super.toString()).append(System.lineSeparator()).append('}');
        return sb.toString();
    }
}
