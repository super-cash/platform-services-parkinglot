package cash.super_.platform.model.supercash.types.charge;

import cash.super_.platform.model.supercash.types.order.OrderQRCode;
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
        final StringBuffer sb = new StringBuffer(System.lineSeparator());
        sb.append("PaymentChargeRequest{");
        sb.append(System.lineSeparator());
        sb.append("  \"qrCodes\": ").append(qrCodes).append(',').append(System.lineSeparator());
        sb.append("  \"paymentMethod\": ").append(paymentMethod).append(',').append(System.lineSeparator());
        sb.append(super.toString()).append(System.lineSeparator()).append('}');
        return sb.toString();
    }
}
