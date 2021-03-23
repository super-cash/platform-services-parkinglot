package cash.super_.platform.error.supercash;

import org.springframework.http.HttpStatus;

public class SupercashTransactionAlreadyPaidSimpleException extends SupercashSimpleException {

    public SupercashTransactionAlreadyPaidSimpleException() {
        super(SupercashErrorCode.ALREADY_PAID, HttpStatus.FORBIDDEN);
    }

    public SupercashTransactionAlreadyPaidSimpleException(HttpStatus additionalErrorCode) {
        super(SupercashErrorCode.ALREADY_PAID, additionalErrorCode);
    }

    public SupercashTransactionAlreadyPaidSimpleException(String additionalMessage) {
        super(SupercashErrorCode.ALREADY_PAID, HttpStatus.FORBIDDEN, additionalMessage);
    }

    public SupercashTransactionAlreadyPaidSimpleException(HttpStatus additionalErrorCode, String additionalMessage) {
        super(SupercashErrorCode.ALREADY_PAID, additionalErrorCode, additionalMessage);
    }
}
