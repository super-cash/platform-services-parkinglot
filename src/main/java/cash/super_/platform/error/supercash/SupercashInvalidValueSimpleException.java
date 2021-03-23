package cash.super_.platform.error.supercash;

import org.springframework.http.HttpStatus;

public class SupercashInvalidValueSimpleException extends SupercashSimpleException {

    public SupercashInvalidValueSimpleException() {
        super(SupercashErrorCode.INVALID_VALUE, HttpStatus.BAD_REQUEST);
    }

    public SupercashInvalidValueSimpleException(HttpStatus additionalErrorCode) {
        super(SupercashErrorCode.INVALID_VALUE, additionalErrorCode);
    }

    public SupercashInvalidValueSimpleException(String additionalMessage) {
        super(SupercashErrorCode.INVALID_VALUE, HttpStatus.BAD_REQUEST, additionalMessage);
    }

    public SupercashInvalidValueSimpleException(HttpStatus additionalErrorCode, String additionalMessage) {
        super(SupercashErrorCode.INVALID_VALUE, additionalErrorCode, additionalMessage);
    }
}
