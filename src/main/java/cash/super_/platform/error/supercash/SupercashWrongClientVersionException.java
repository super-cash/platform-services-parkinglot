package cash.super_.platform.error.supercash;

import org.springframework.http.HttpStatus;

public class SupercashWrongClientVersionException extends SupercashSimpleException {

    public SupercashWrongClientVersionException() {
        super(SupercashErrorCode.WRONG_CLIENT_VERSION, HttpStatus.BAD_REQUEST);
    }

    public SupercashWrongClientVersionException(HttpStatus additionalErrorCode) {
        super(SupercashErrorCode.WRONG_CLIENT_VERSION, additionalErrorCode);
    }

    public SupercashWrongClientVersionException(String additionalMessage) {
        super(SupercashErrorCode.WRONG_CLIENT_VERSION, HttpStatus.BAD_REQUEST, additionalMessage);
    }

    public SupercashWrongClientVersionException(HttpStatus additionalErrorCode, String additionalMessage) {
        super(SupercashErrorCode.WRONG_CLIENT_VERSION, additionalErrorCode, additionalMessage);
    }
}
