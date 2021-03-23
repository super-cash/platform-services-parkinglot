package cash.super_.platform.error.supercash;

import org.springframework.http.HttpStatus;

public class SupercashThirdPartySystemSimpleException extends SupercashSimpleException {

    public SupercashThirdPartySystemSimpleException() {
        super(SupercashErrorCode.THIRD_PARTY_EXCEPTION, HttpStatus.FAILED_DEPENDENCY);
    }

    public SupercashThirdPartySystemSimpleException(HttpStatus additionalErrorCode) {
        super(SupercashErrorCode.THIRD_PARTY_EXCEPTION, additionalErrorCode);
    }

    public SupercashThirdPartySystemSimpleException(String additionalMessage) {
        super(SupercashErrorCode.THIRD_PARTY_EXCEPTION, HttpStatus.FAILED_DEPENDENCY, additionalMessage);
    }

    public SupercashThirdPartySystemSimpleException(HttpStatus additionalErrorCode, String additionalMessage) {
        super(SupercashErrorCode.THIRD_PARTY_EXCEPTION, additionalErrorCode, additionalMessage);
    }
}
