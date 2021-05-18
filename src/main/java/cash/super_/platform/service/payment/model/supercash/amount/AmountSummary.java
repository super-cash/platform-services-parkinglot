package cash.super_.platform.service.payment.model.supercash.amount;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.Embeddable;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Embeddable
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AmountSummary {

    @Max(value = 999999999)
    @Min(value = -999999999)
    private Long total;

    @Max(value = 999999999)
    @Min(value = -999999999)
    private Long paid;

    @Max(value = 999999999)
    @Min(value = -999999999)
    private Long refunded;

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
        final StringBuffer sb = new StringBuffer(System.lineSeparator());
        sb.append("AmountSummary{");
        sb.append(System.lineSeparator());
        sb.append("  \"total\": ").append(total).append(',').append(System.lineSeparator());
        sb.append("  \"paid\": ").append(paid).append(',').append(System.lineSeparator());
        sb.append("  \"refunded\": ").append(refunded).append(',').append(System.lineSeparator());
        sb.append(super.toString()).append(System.lineSeparator()).append('}');
        return sb.toString();
    }
}
