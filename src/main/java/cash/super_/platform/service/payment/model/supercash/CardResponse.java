package cash.super_.platform.service.payment.model.supercash;

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
        return "Card{" +
                "brand='" + brand + '\'' +
                ", first_digits='" + firstDigits + '\'' +
                ", last_digits='" + lastDigits + '\'' +
                ", exp_month='" + expMonth + '\'' +
                ", exp_year='" + expYear + '\'' +
                ", holder=" + holder +
                '}';
    }
}
