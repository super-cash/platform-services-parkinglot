package cash.super_.platform.error.supercash;

import cash.super_.platform.error.supercash.SupercashErrorCode;
import cash.super_.platform.error.supercash.SupercashSimpleException;
import org.springframework.http.HttpStatus;

public class SupercashInvalidValueException extends SupercashSimpleException {

    public SupercashInvalidValueException() {
        super(SupercashErrorCode.INVALID_VALUE, HttpStatus.BAD_REQUEST);
    }

    public SupercashInvalidValueException(HttpStatus additionalErrorCode) {
        super(SupercashErrorCode.INVALID_VALUE, additionalErrorCode);
    }

    public SupercashInvalidValueException(String additionalMessage) {
        super(SupercashErrorCode.INVALID_VALUE, HttpStatus.BAD_REQUEST, additionalMessage);
    }

    public SupercashInvalidValueException(HttpStatus additionalErrorCode, String additionalMessage) {
        super(SupercashErrorCode.INVALID_VALUE, additionalErrorCode, additionalMessage);
    }
}
