package cash.super_.platform.error.parkinglot;

import cash.super_.platform.adapter.feign.SupercashErrorCode;
import cash.super_.platform.adapter.feign.SupercashSimpleException;
import org.springframework.http.HttpStatus;

public class SupercashPaymentErrorException extends SupercashSimpleException {

    public SupercashPaymentErrorException() {
        super(SupercashErrorCode.PAYMENT_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public SupercashPaymentErrorException(HttpStatus additionalErrorCode) {
        super(SupercashErrorCode.PAYMENT_ERROR, additionalErrorCode);
    }

    public SupercashPaymentErrorException(String additionalMessage) {
        super(SupercashErrorCode.PAYMENT_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, additionalMessage);
    }

    public SupercashPaymentErrorException(HttpStatus additionalErrorCode, String additionalMessage) {
        super(SupercashErrorCode.PAYMENT_ERROR, additionalErrorCode, additionalMessage);
    }
}
