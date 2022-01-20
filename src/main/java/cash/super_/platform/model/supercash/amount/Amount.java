package cash.super_.platform.model.supercash.amount;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity(name = "payment_amount")
//@Embeddable
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Amount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Max(value = 999999999)
    @Min(value = -999999999)
    @NotNull
    @JsonProperty(required = true)
    private Long value;

    @JsonProperty(value = "currency")
    private AmountCurrency currency = AmountCurrency.BRL;

    @Embedded
    @JsonProperty(value = "summary")
    private AmountSummary summary;

    public Amount() { }

    public Amount(Long value) {
        this.value = value;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public AmountCurrency getCurrency() {
        return currency;
    }

    public void setCurrency(AmountCurrency currency) {
        this.currency = currency;
    }

    public AmountSummary getSummary() {
        return summary;
    }

    public void setSummary(AmountSummary summary) {
        this.summary = summary;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(System.lineSeparator());
        sb.append("Amount{");
        sb.append(System.lineSeparator());
        sb.append("  \"id\": ").append(id).append(',').append(System.lineSeparator());
        sb.append("  \"value\": ").append(value).append(',').append(System.lineSeparator());
        sb.append("  \"currency\": ").append(currency).append(',').append(System.lineSeparator());
        sb.append("  \"summary\": ").append(summary).append(',').append(System.lineSeparator());
        sb.append(System.lineSeparator()).append('}');
        return sb.toString();
    }
}
