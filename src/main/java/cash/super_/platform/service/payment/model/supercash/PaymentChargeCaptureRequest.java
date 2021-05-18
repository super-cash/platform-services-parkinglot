package cash.super_.platform.service.payment.model.supercash;

import cash.super_.platform.service.payment.model.supercash.amount.Amount;

public class PaymentChargeCaptureRequest {

    private Payment.PaymentGateway gateway = Payment.PaymentGateway.PAGSEGURO;

    private Amount amount;

    public Payment.PaymentGateway getGateway() {
        return gateway;
    }

    public void setGateway(Payment.PaymentGateway gateway) {
        this.gateway = gateway;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(System.lineSeparator());
        sb.append("PaymentChargeCaptureRequest{");
        sb.append(System.lineSeparator());
        sb.append("  \"gateway\": ").append(gateway).append(',').append(System.lineSeparator());
        sb.append("  \"amount\": ").append(amount).append(',').append(System.lineSeparator());
        sb.append(System.lineSeparator()).append('}');
        return sb.toString();
    }
}
