package cash.super_.platform.service.payment.model.pagseguro;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class Summary{

    @Max(value = 999999999)
    @Min(value = -999999999)
    public Long total;

    @Max(value = 999999999)
    @Min(value = -999999999)
    public Long paid;

    @Max(value = 999999999)
    @Min(value = -999999999)
    public Long refunded;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getPaid() {
        return paid;
    }

    public void setPaid(Long paid) {
        this.paid = paid;
    }

    public Long getRefunded() {
        return refunded;
    }

    public void setRefunded(Long refunded) {
        this.refunded = refunded;
    }

    @Override
    public String toString() {
        return "Summary{" +
                "total=" + total +
                ", paid=" + paid +
                ", refunded=" + refunded +
                '}';
    }
}
