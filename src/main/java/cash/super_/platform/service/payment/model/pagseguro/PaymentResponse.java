package cash.super_.platform.service.payment.model.pagseguro;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentResponse {

    @Max(value = 99999)
    @Min(value = 0)
    public Integer code;

    @Length(min = 5, max = 100)
    public String message;

    @Length(min = 4, max = 20)
    private String reference;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    @Override
    public String toString() {
        return "PaymentResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", reference='" + reference + '\'' +
                '}';
    }
}
