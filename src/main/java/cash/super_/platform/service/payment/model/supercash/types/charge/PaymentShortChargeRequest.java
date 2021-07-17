package cash.super_.platform.service.payment.model.supercash.types.charge;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class PaymentShortChargeRequest extends PaymentCharge {

    @JsonProperty(value = "payment_method")
    public ChargePaymentMethodRequest paymentMethod;

    public PaymentShortChargeRequest() {
        this.metadata.put("uuid", UUID.randomUUID().toString());
    }

    public PaymentShortChargeRequest(UUID uuid) {
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
        final StringBuffer sb = new StringBuffer(System.lineSeparator());
        sb.append("PaymentChargeRequest{");
        sb.append(System.lineSeparator());
        sb.append("  \"paymentMethod\": ").append(paymentMethod).append(',').append(System.lineSeparator());
        sb.append(super.toString()).append(System.lineSeparator()).append('}');
        return sb.toString();
    }
}
