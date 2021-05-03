package cash.super_.platform.service.payment.model.pagseguro;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Size;
import java.util.List;

public class ChargeRequest extends Charge {

    @JsonProperty(value = "qr_codes")
    @Size(max = 1)
    private List<QRCode> qrCodes;

    @JsonProperty(value = "payment_method")
    public PaymentMethodRequest paymentMethod;

    public PaymentMethodRequest getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethodRequest paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @Override
    public String toString() {
        return "ChargeRequest{" +
                ", paymentMethod=" + paymentMethod +
                "} " + super.toString();
    }
}
