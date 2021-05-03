package cash.super_.platform.service.payment.model.pagseguro;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardRequest {

    @JsonProperty(required = true)
    private String number;

    @JsonProperty(value = "exp_month", required = true)
    private Integer expMonth;

    @JsonProperty(value = "exp_year", required = true)
    private Integer expYear;

    @JsonProperty(value = "security_code", required = true)
    private Integer securityCode;

    @JsonProperty(required = true)
    private Holder holder;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Integer getSecurityCode() {
        return securityCode;
    }

    public void getSecurityCode(Integer firstDigits) {
        this.securityCode = firstDigits;
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
                "number='" + number + '\'' +
                ", security_code='" + securityCode + '\'' +
                ", exp_month='" + expMonth + '\'' +
                ", exp_year='" + expYear + '\'' +
                ", holder=" + holder +
                '}';
    }
}
