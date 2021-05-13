package cash.super_.platform.service.payment.model.supercash;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

public class PaymentChargeRequest extends PaymentCharge {

    @JsonProperty(value = "qr_codes")
    @Size(max = 1)
    private List<OrderQRCode> qrCodes;

    @JsonProperty(value = "payment_method")
    public ChargePaymentMethodRequest paymentMethod;

    public PaymentChargeRequest() {
        this.metadata.put("uuid", UUID.randomUUID().toString());
    }

    public PaymentChargeRequest(UUID uuid) {
        this.metadata.put("uuid", uuid.toString());
    }

    public ChargePaymentMethodRequest getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(ChargePaymentMethodRequest paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @Override
    public String toString() {
        return "ChargeRequest{" +
                ", paymentMethod=" + paymentMethod +
                "} " + super.toString();
    }
}
