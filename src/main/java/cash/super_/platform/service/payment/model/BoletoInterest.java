package cash.super_.platform.service.payment.model;

import java.util.Objects;

public class BoletoInterest {

    /**
     * Dias após a expiração do boleto quando a multa deve ser cobrada.
     */
    private Integer days;

    /**
     * Valor em centavos da multa.
     */
    private Integer amount;

    /**
     * Valor em porcentagem da multa.
     */
    private Integer percentage;

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getPercentage() {
        return percentage;
    }

    public void setPercentage(Integer percentage) {
        this.percentage = percentage;
    }

    @Override
    public String toString() {
        return "BoletoInterest{" +
                "days=" + days +
                ", amount=" + amount +
                ", percentage=" + percentage +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BoletoInterest)) return false;
        BoletoInterest that = (BoletoInterest) o;
        return getDays().equals(that.getDays()) && getAmount().equals(that.getAmount()) && getPercentage().equals(that.getPercentage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDays(), getAmount(), getPercentage());
    }
}
