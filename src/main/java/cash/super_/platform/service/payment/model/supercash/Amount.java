package cash.super_.platform.service.payment.model.supercash;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity(name = "payment_amount")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Amount {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
        return "Amount{" +
                "value=" + value +
                ", currency='" + currency + '\'' +
                ", summary=" + summary +
                '}';
    }
}
