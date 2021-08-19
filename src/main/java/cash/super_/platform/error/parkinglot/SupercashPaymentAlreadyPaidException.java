package cash.super_.platform.error.parkinglot;

import cash.super_.platform.adapter.feign.SupercashErrorCode;
import cash.super_.platform.adapter.feign.SupercashSimpleException;
import org.springframework.http.HttpStatus;

public class SupercashPaymentAlreadyPaidException extends SupercashSimpleException {

    public SupercashPaymentAlreadyPaidException() {
        super(SupercashErrorCode.ALREADY_PAID, HttpStatus.FORBIDDEN);
    }

    public SupercashPaymentAlreadyPaidException(HttpStatus additionalErrorCode) {
        super(SupercashErrorCode.ALREADY_PAID, additionalErrorCode);
    }

    public SupercashPaymentAlreadyPaidException(String additionalMessage) {
        super(SupercashErrorCode.ALREADY_PAID, HttpStatus.FORBIDDEN, additionalMessage);
    }

    public SupercashPaymentAlreadyPaidException(HttpStatus additionalErrorCode, String additionalMessage) {
        super(SupercashErrorCode.ALREADY_PAID, additionalErrorCode, additionalMessage);
    }
}
