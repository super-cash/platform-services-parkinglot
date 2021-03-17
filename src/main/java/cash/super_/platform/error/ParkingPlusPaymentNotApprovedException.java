package cash.super_.platform.error;

import org.springframework.http.HttpStatus;

public class ParkingPlusPaymentNotApprovedException extends SupercashException {

    public ParkingPlusPaymentNotApprovedException() {
        super(SupercashErrorCode.PAYMENT_NOT_APPROVED, HttpStatus.FORBIDDEN);
    }

    public ParkingPlusPaymentNotApprovedException(HttpStatus additionalErrorCode) {
        super(SupercashErrorCode.PAYMENT_NOT_APPROVED, additionalErrorCode);
    }

    public ParkingPlusPaymentNotApprovedException(String additionalMessage) {
        super(SupercashErrorCode.PAYMENT_NOT_APPROVED, HttpStatus.FORBIDDEN, additionalMessage);
    }

    public ParkingPlusPaymentNotApprovedException(HttpStatus additionalErrorCode, String additionalMessage) {
        super(SupercashErrorCode.PAYMENT_NOT_APPROVED, additionalErrorCode, additionalMessage);
    }
}
