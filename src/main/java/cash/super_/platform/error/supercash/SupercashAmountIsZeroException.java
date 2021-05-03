package cash.super_.platform.error.supercash;

import org.springframework.http.HttpStatus;

public class SupercashAmountIsZeroException extends SupercashSimpleException {

    public SupercashAmountIsZeroException() {
        super(SupercashErrorCode.AMOUNT_IS_ZERO, HttpStatus.UNAUTHORIZED);
    }

    public SupercashAmountIsZeroException(HttpStatus additionalErrorCode) {
        super(SupercashErrorCode.AMOUNT_IS_ZERO, additionalErrorCode);
    }

    public SupercashAmountIsZeroException(String additionalMessage) {
        super(SupercashErrorCode.AMOUNT_IS_ZERO, HttpStatus.UNAUTHORIZED, additionalMessage);
    }

    public SupercashAmountIsZeroException(HttpStatus additionalErrorCode, String additionalMessage) {
        super(SupercashErrorCode.AMOUNT_IS_ZERO, additionalErrorCode, additionalMessage);
    }
}
