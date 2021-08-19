package cash.super_.platform.error.parkinglot;

import cash.super_.platform.adapter.feign.SupercashErrorCode;
import cash.super_.platform.adapter.feign.SupercashSimpleException;
import org.springframework.http.HttpStatus;

public class SupercashMarketplaceNotFoundException extends SupercashSimpleException {

    public SupercashMarketplaceNotFoundException() {
        super(SupercashErrorCode.MARKETPLACE_NOT_FOUND, HttpStatus.BAD_REQUEST);
    }

    public SupercashMarketplaceNotFoundException(HttpStatus additionalErrorCode) {
        super(SupercashErrorCode.MARKETPLACE_NOT_FOUND, additionalErrorCode);
    }

    public SupercashMarketplaceNotFoundException(String additionalMessage) {
        super(SupercashErrorCode.MARKETPLACE_NOT_FOUND, HttpStatus.BAD_REQUEST, additionalMessage);
    }

    public SupercashMarketplaceNotFoundException(HttpStatus additionalErrorCode, String additionalMessage) {
        super(SupercashErrorCode.MARKETPLACE_NOT_FOUND, additionalErrorCode, additionalMessage);
    }
}
