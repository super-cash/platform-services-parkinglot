package cash.super_.platform.model.supercash;

import cash.super_.platform.model.supercash.amount.Amount;

public class PaymentChargeCaptureRequest {

    private PaymentGateway gateway = PaymentGateway.PAGSEGURO;

    private Amount amount;

    public PaymentGateway getGateway() {
        return gateway;
    }

    public void setGateway(PaymentGateway gateway) {
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
