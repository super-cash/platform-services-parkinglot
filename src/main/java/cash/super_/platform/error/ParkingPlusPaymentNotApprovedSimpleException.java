package cash.super_.platform.error;

import cash.super_.platform.error.supercash.SupercashErrorCode;
import cash.super_.platform.error.supercash.SupercashSimpleException;
import org.springframework.http.HttpStatus;

public class ParkingPlusPaymentNotApprovedSimpleException extends SupercashSimpleException {

    public ParkingPlusPaymentNotApprovedSimpleException() {
        super(SupercashErrorCode.PAYMENT_NOT_APPROVED, HttpStatus.FORBIDDEN);
    }

    public ParkingPlusPaymentNotApprovedSimpleException(HttpStatus additionalErrorCode) {
        super(SupercashErrorCode.PAYMENT_NOT_APPROVED, additionalErrorCode);
    }

    public ParkingPlusPaymentNotApprovedSimpleException(String additionalMessage) {
        super(SupercashErrorCode.PAYMENT_NOT_APPROVED, HttpStatus.FORBIDDEN, additionalMessage);
    }

    public ParkingPlusPaymentNotApprovedSimpleException(HttpStatus additionalErrorCode, String additionalMessage) {
        super(SupercashErrorCode.PAYMENT_NOT_APPROVED, additionalErrorCode, additionalMessage);
    }
}
