package cash.super_.platform.service.payment.model.pagseguro;

import cash.super_.platform.service.payment.model.pagarme.Transaction;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Status enum
 */
public enum ChargeStatus {

    /**
     * AUTHORIZED
     */
    AUTHORIZED,

    /**
     * PAID
     */
    PAID,

    /**
     * WAITING (only for boleto)
     */
    WAITING,

    /**
     * DECLINED
     */
    DECLINED,

    /**
     * CANCELED
     */
    CANCELED;

    private ChargeStatus status;

    @JsonGetter(value = "status")
    public ChargeStatus value() {
        return this.status;
    }

    public void setValue(ChargeStatus status) {
        this.status = status;
    }

    public Transaction.Status toPagarmeStatus() {
        switch (status) {
            case AUTHORIZED:
                return Transaction.Status.AUTHORIZED;

            case PAID:
                return Transaction.Status.PAID;

            case CANCELED:
                return Transaction.Status.REFUSED;

            case DECLINED:
                return Transaction.Status.REFUSED;

            case WAITING:
                return Transaction.Status.WAITING_PAYMENT;
        }
        return null;
    }

    @Override
    public String toString() {
        return "ChargeStatus{" +
                "status=" + status +
                "} ";
    }
}