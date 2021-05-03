package cash.super_.platform.service.payment.model.pagseguro;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardResponse {

    private String brand;

    @JsonProperty(value = "first_digits")
    private Integer firstDigits;

    @JsonProperty(value = "last_digits")
    private Integer lastDigits;

    @JsonProperty(value = "exp_month")
    private Integer expMonth;

    @JsonProperty(value = "exp_year")
    private Integer expYear;

    private Holder holder;

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

    public Holder getHolder() {
        return holder;
    }

    public void setHolder(Holder holder) {
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
