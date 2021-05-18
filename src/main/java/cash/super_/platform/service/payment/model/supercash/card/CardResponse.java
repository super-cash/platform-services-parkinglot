package cash.super_.platform.service.payment.model.supercash.card;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity(name = "payment_card_response")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardResponse extends Card {

    private String brand;

    @JsonProperty(value = "first_digits")
    private Integer firstDigits;

    @JsonProperty(value = "last_digits")
    private Integer lastDigits;

    @JsonProperty(value = "exp_month")
    private Integer expMonth;

    @JsonProperty(value = "exp_year")
    private Integer expYear;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonProperty(value = "holder")
    private CardHolder holder;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Integer getFirstDigits() {
        return firstDigits;
    }

    public void setFirstDigits(Integer firstDigits) {
        this.firstDigits = firstDigits;
    }

    public Integer getLastDigits() {
        return lastDigits;
    }

    public void setLastDigits(Integer lastDigits) {
        this.lastDigits = lastDigits;
    }

    public Integer getExpMonth() {
        return expMonth;
    }

    public void setExpMonth(Integer expMonth) {
        this.expMonth = expMonth;
    }

    public Integer getExpYear() {
        return expYear;
    }

    public void setExpYear(Integer expYear) {
        this.expYear = expYear;
    }

    public CardHolder getHolder() {
        return holder;
    }

    public void setHolder(CardHolder holder) {
        this.holder = holder;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(System.lineSeparator());
        sb.append("CardResponse{");
        sb.append(System.lineSeparator());
        sb.append("  \"brand\": \"").append(brand).append("\",").append(System.lineSeparator());
        sb.append("  \"firstDigits\": ").append(firstDigits).append(',').append(System.lineSeparator());
        sb.append("  \"lastDigits\": ").append(lastDigits).append(',').append(System.lineSeparator());
        sb.append("  \"expMonth\": ").append(expMonth).append(',').append(System.lineSeparator());
        sb.append("  \"expYear\": ").append(expYear).append(',').append(System.lineSeparator());
        sb.append("  \"holder\": ").append(holder).append(',').append(System.lineSeparator());
        sb.append(super.toString()).append(System.lineSeparator()).append('}');
        return sb.toString();
    }
}
