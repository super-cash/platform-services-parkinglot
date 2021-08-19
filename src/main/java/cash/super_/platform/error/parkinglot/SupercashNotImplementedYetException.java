package cash.super_.platform.error.parkinglot;

import cash.super_.platform.adapter.feign.SupercashErrorCode;
import cash.super_.platform.adapter.feign.SupercashSimpleException;
import org.springframework.http.HttpStatus;

public class SupercashNotImplementedYetException extends SupercashSimpleException {

    public SupercashNotImplementedYetException() {
        super(SupercashErrorCode.NOT_IMPLEMENTED_YET, HttpStatus.BAD_REQUEST);
    }

    public SupercashNotImplementedYetException(HttpStatus additionalErrorCode) {
        super(SupercashErrorCode.NOT_IMPLEMENTED_YET, additionalErrorCode);
    }

    public SupercashNotImplementedYetException(String additionalMessage) {
        super(SupercashErrorCode.NOT_IMPLEMENTED_YET, HttpStatus.BAD_REQUEST, additionalMessage);
    }

    public SupercashNotImplementedYetException(HttpStatus additionalErrorCode, String additionalMessage) {
        super(SupercashErrorCode.NOT_IMPLEMENTED_YET, additionalErrorCode, additionalMessage);
    }
}
