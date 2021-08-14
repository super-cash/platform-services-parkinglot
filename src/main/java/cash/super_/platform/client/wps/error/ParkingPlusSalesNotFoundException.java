package cash.super_.platform.client.wps.error;

import cash.super_.platform.error.parkinglot.SupercashErrorCode;
import cash.super_.platform.error.parkinglot.SupercashSimpleException;
import org.springframework.http.HttpStatus;

public class ParkingPlusSalesNotFoundException extends SupercashSimpleException {

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
