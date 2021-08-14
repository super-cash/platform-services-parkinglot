package cash.super_.platform.model.payment.pagarme;

import java.util.Objects;

public class BoletoFine {

    /**
     * Dias após a expiração do boleto quando a multa deve ser cobrada.
     */
    private Integer days;

    /**
     * Valor em centavos da multa.
     */
    private Long amount;

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

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
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
        return "BoletoFine{" +
                "days=" + days +
                ", amount=" + amount +
                ", percentage=" + percentage +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BoletoFine)) return false;
        BoletoFine that = (BoletoFine) o;
        return getDays().equals(that.getDays()) && getAmount().equals(that.getAmount()) && getPercentage().equals(that.getPercentage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDays(), getAmount(), getPercentage());
    }
}
