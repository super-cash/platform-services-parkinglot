package cash.super_.platform.error.parkinglot;

import org.springframework.http.HttpStatus;

public class SupercashThirdPartySystemException extends SupercashSimpleException {

    public SupercashThirdPartySystemException() {
        super(SupercashErrorCode.THIRD_PARTY_EXCEPTION, HttpStatus.FAILED_DEPENDENCY);
    }

    public SupercashThirdPartySystemException(HttpStatus additionalErrorCode) {
        super(SupercashErrorCode.THIRD_PARTY_EXCEPTION, additionalErrorCode);
    }

    public SupercashThirdPartySystemException(String additionalMessage) {
        super(SupercashErrorCode.THIRD_PARTY_EXCEPTION, HttpStatus.FAILED_DEPENDENCY, additionalMessage);
    }

    public SupercashThirdPartySystemException(HttpStatus additionalErrorCode, String additionalMessage) {
        super(SupercashErrorCode.THIRD_PARTY_EXCEPTION, additionalErrorCode, additionalMessage);
    }
}
