package cash.super_.platform.error.parkinglot;

import cash.super_.platform.adapter.feign.SupercashErrorCode;
import cash.super_.platform.adapter.feign.SupercashSimpleException;
import org.springframework.http.HttpStatus;

public class SupercashMissingArgumentException extends SupercashSimpleException {

    public SupercashMissingArgumentException() {
        super(SupercashErrorCode.MISSING_ARGUMENT, HttpStatus.BAD_REQUEST);
    }

    public SupercashMissingArgumentException(HttpStatus additionalErrorCode) {
        super(SupercashErrorCode.MISSING_ARGUMENT, additionalErrorCode);
    }

    public SupercashMissingArgumentException(String additionalMessage) {
        super(SupercashErrorCode.MISSING_ARGUMENT, HttpStatus.BAD_REQUEST, additionalMessage);
    }

    public SupercashMissingArgumentException(HttpStatus additionalErrorCode, String additionalMessage) {
        super(SupercashErrorCode.MISSING_ARGUMENT, additionalErrorCode, additionalMessage);
    }
}
