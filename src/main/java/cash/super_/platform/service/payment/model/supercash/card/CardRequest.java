package cash.super_.platform.service.payment.model.supercash.card;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardRequest extends Card {

    @JsonProperty(value = "encrypted")
    private String encrypted;

    private Boolean store;

    @JsonProperty
    private String number;

    @JsonProperty(value = "exp_month")
    private Integer expMonth;

    @JsonProperty(value = "exp_year")
    private Integer expYear;

    @JsonProperty(value = "security_code")
    private Integer securityCode;

    @JsonProperty(required = true)
    private CardHolder holder;

    public void setEncrypted(String encrypted) {
        this.encrypted = encrypted;
    }

    @JsonIgnore
    public String getEncrypted() {
        return encrypted;
    }

    public Boolean getStore() {
        return store;
    }

    public void setStore(Boolean store) {
        this.store = store;
    }

    public void setSecurityCode(Integer securityCode) {
        this.securityCode = securityCode;
    }

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

    public CardHolder getHolder() {
        return holder;
    }

    public void setHolder(CardHolder holder) {
        this.holder = holder;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(System.lineSeparator());
        sb.append("CardRequest{");
        sb.append(System.lineSeparator());
        sb.append("  \"encrypted\": \"").append(encrypted).append("\",").append(System.lineSeparator());
        sb.append("  \"store\": ").append(store).append(',').append(System.lineSeparator());
        sb.append("  \"number\": \"").append(number).append("\",").append(System.lineSeparator());
        sb.append("  \"expMonth\": ").append(expMonth).append(',').append(System.lineSeparator());
        sb.append("  \"expYear\": ").append(expYear).append(',').append(System.lineSeparator());
        sb.append("  \"securityCode\": ").append(securityCode).append(',').append(System.lineSeparator());
        sb.append("  \"holder\": ").append(holder).append(',').append(System.lineSeparator());
        sb.append(super.toString()).append(System.lineSeparator()).append('}');
        return sb.toString();
    }
}
