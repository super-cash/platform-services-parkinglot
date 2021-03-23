package cash.super_.platform.error;

import cash.super_.platform.error.supercash.SupercashErrorCode;
import cash.super_.platform.error.supercash.SupercashSimpleException;
import org.springframework.http.HttpStatus;

public class ParkingPlusInvalidSalesSimpleException extends SupercashSimpleException {

    public ParkingPlusInvalidSalesSimpleException() {
        super(SupercashErrorCode.INVALID_SALE, HttpStatus.UNAUTHORIZED);
    }

    public ParkingPlusInvalidSalesSimpleException(HttpStatus additionalErrorCode) {
        super(SupercashErrorCode.INVALID_SALE, additionalErrorCode);
    }

    public ParkingPlusInvalidSalesSimpleException(String additionalMessage) {
        super(SupercashErrorCode.INVALID_SALE, HttpStatus.UNAUTHORIZED, additionalMessage);
    }

    public ParkingPlusInvalidSalesSimpleException(HttpStatus additionalErrorCode, String additionalMessage) {
        super(SupercashErrorCode.INVALID_SALE, additionalErrorCode, additionalMessage);
    }
}
