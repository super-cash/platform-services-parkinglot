package cash.super_.platform.error.supercash;

import cash.super_.platform.error.supercash.SupercashErrorCode;
import cash.super_.platform.error.supercash.SupercashSimpleException;
import org.springframework.http.HttpStatus;

public class SupercashUnknownHostException extends SupercashSimpleException {

    public SupercashUnknownHostException() {
        super(SupercashErrorCode.UNKNOWN_HOST, HttpStatus.FAILED_DEPENDENCY);
    }

    public SupercashUnknownHostException(HttpStatus additionalErrorCode) {
        super(SupercashErrorCode.UNKNOWN_HOST, additionalErrorCode);
    }

    public SupercashUnknownHostException(String additionalMessage) {
        super(SupercashErrorCode.UNKNOWN_HOST, HttpStatus.FAILED_DEPENDENCY, additionalMessage);
    }

    public SupercashUnknownHostException(HttpStatus additionalErrorCode, String additionalMessage) {
        super(SupercashErrorCode.UNKNOWN_HOST, additionalErrorCode, additionalMessage);
    }
}
