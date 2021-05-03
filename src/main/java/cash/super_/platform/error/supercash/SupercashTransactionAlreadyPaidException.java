package cash.super_.platform.error.supercash;

import org.springframework.http.HttpStatus;

public class SupercashTransactionAlreadyPaidException extends SupercashSimpleException {

    public SupercashTransactionAlreadyPaidException() {
        super(SupercashErrorCode.ALREADY_PAID, HttpStatus.FORBIDDEN);
    }

    public SupercashTransactionAlreadyPaidException(HttpStatus additionalErrorCode) {
        super(SupercashErrorCode.ALREADY_PAID, additionalErrorCode);
    }

    public SupercashTransactionAlreadyPaidException(String additionalMessage) {
        super(SupercashErrorCode.ALREADY_PAID, HttpStatus.FORBIDDEN, additionalMessage);
    }

    public SupercashTransactionAlreadyPaidException(HttpStatus additionalErrorCode, String additionalMessage) {
        super(SupercashErrorCode.ALREADY_PAID, additionalErrorCode, additionalMessage);
    }
}
