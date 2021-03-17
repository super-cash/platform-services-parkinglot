package cash.super_.platform.error;

import org.springframework.http.HttpStatus;

public class ParkingPlusInvalidSalesException extends SupercashException {

    public ParkingPlusInvalidSalesException() {
        super(SupercashErrorCode.INVALID_SALE, HttpStatus.BAD_REQUEST);
    }

    public ParkingPlusInvalidSalesException(HttpStatus additionalErrorCode) {
        super(SupercashErrorCode.INVALID_SALE, additionalErrorCode);
    }

    public ParkingPlusInvalidSalesException(String additionalMessage) {
        super(SupercashErrorCode.INVALID_SALE, HttpStatus.BAD_REQUEST, additionalMessage);
    }

    public ParkingPlusInvalidSalesException(HttpStatus additionalErrorCode, String additionalMessage) {
        super(SupercashErrorCode.INVALID_SALE, additionalErrorCode, additionalMessage);
    }
}
