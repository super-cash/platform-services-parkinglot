package cash.super_.platform.error;

import cash.super_.platform.error.supercash.SupercashErrorCode;
import cash.super_.platform.error.supercash.SupercashException;
import org.springframework.http.HttpStatus;

public class ParkingPlusInvalidSalesException extends SupercashException {

    public ParkingPlusInvalidSalesException() {
        super(SupercashErrorCode.INVALID_SALE, HttpStatus.UNAUTHORIZED);
    }

    public ParkingPlusInvalidSalesException(HttpStatus additionalErrorCode) {
        super(SupercashErrorCode.INVALID_SALE, additionalErrorCode);
    }

    public ParkingPlusInvalidSalesException(String additionalMessage) {
        super(SupercashErrorCode.INVALID_SALE, HttpStatus.UNAUTHORIZED, additionalMessage);
    }

    public ParkingPlusInvalidSalesException(HttpStatus additionalErrorCode, String additionalMessage) {
        super(SupercashErrorCode.INVALID_SALE, additionalErrorCode, additionalMessage);
    }
}
