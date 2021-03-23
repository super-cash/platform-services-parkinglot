package cash.super_.platform.error;

import cash.super_.platform.error.supercash.SupercashErrorCode;
import cash.super_.platform.error.supercash.SupercashSimpleException;
import org.springframework.http.HttpStatus;

public class ParkingPlusSalesNotFoundSimpleException extends SupercashSimpleException {

    public ParkingPlusSalesNotFoundSimpleException() {
        super(SupercashErrorCode.SALE_NOT_FOUND, HttpStatus.NOT_FOUND);
    }

    public ParkingPlusSalesNotFoundSimpleException(HttpStatus additionalErrorCode) {
        super(SupercashErrorCode.SALE_NOT_FOUND, additionalErrorCode);
    }

    public ParkingPlusSalesNotFoundSimpleException(String additionalMessage) {
        super(SupercashErrorCode.SALE_NOT_FOUND, HttpStatus.NOT_FOUND, additionalMessage);
    }

    public ParkingPlusSalesNotFoundSimpleException(HttpStatus additionalErrorCode, String additionalMessage) {
        super(SupercashErrorCode.SALE_NOT_FOUND, additionalErrorCode, additionalMessage);
    }
}
