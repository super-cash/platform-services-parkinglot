package cash.super_.platform.error.parkinglot;

import org.springframework.http.HttpStatus;

public class SupercashPaymentCantPayInNonNotPaidStateException extends SupercashSimpleException {

    public SupercashPaymentCantPayInNonNotPaidStateException() {
        super(SupercashErrorCode.PAYMENT_ERROR, HttpStatus.FORBIDDEN);
    }

    public SupercashPaymentCantPayInNonNotPaidStateException(HttpStatus additionalErrorCode) {
        super(SupercashErrorCode.PAYMENT_ERROR, additionalErrorCode);
    }

    public SupercashPaymentCantPayInNonNotPaidStateException(String additionalMessage) {
        super(SupercashErrorCode.PAYMENT_ERROR, HttpStatus.FORBIDDEN, additionalMessage);
    }

    public SupercashPaymentCantPayInNonNotPaidStateException(HttpStatus additionalErrorCode, String additionalMessage) {
        super(SupercashErrorCode.PAYMENT_ERROR, additionalErrorCode, additionalMessage);
    }
}
