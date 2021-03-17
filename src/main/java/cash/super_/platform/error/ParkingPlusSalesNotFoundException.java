package cash.super_.platform.error;

import org.springframework.http.HttpStatus;

public class ParkingPlusSalesNotFoundException extends SupercashException {

    public ParkingPlusSalesNotFoundException() {
        super(SupercashErrorCode.SALE_NOT_FOUND, HttpStatus.NOT_FOUND);
    }

    public ParkingPlusSalesNotFoundException(HttpStatus additionalErrorCode) {
        super(SupercashErrorCode.SALE_NOT_FOUND, additionalErrorCode);
    }

    public ParkingPlusSalesNotFoundException(String additionalMessage) {
        super(SupercashErrorCode.SALE_NOT_FOUND, HttpStatus.NOT_FOUND, additionalMessage);
    }

    public ParkingPlusSalesNotFoundException(HttpStatus additionalErrorCode, String additionalMessage) {
        super(SupercashErrorCode.SALE_NOT_FOUND, additionalErrorCode, additionalMessage);
    }
}
