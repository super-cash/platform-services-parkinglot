package cash.super_.platform.error.supercash;

import org.springframework.http.HttpStatus;

public class SupercashAmountIsZeroSimpleException extends SupercashSimpleException {

    public SupercashAmountIsZeroSimpleException() {
        super(SupercashErrorCode.AMOUNT_IS_ZERO, HttpStatus.UNAUTHORIZED);
    }

    public SupercashAmountIsZeroSimpleException(HttpStatus additionalErrorCode) {
        super(SupercashErrorCode.AMOUNT_IS_ZERO, additionalErrorCode);
    }

    public SupercashAmountIsZeroSimpleException(String additionalMessage) {
        super(SupercashErrorCode.AMOUNT_IS_ZERO, HttpStatus.UNAUTHORIZED, additionalMessage);
    }

    public SupercashAmountIsZeroSimpleException(HttpStatus additionalErrorCode, String additionalMessage) {
        super(SupercashErrorCode.AMOUNT_IS_ZERO, additionalErrorCode, additionalMessage);
    }
}
