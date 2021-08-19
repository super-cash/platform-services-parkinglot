package cash.super_.platform.client.wps.error;

import cash.super_.platform.adapter.feign.SupercashErrorCode;
import cash.super_.platform.adapter.feign.SupercashSimpleException;
import org.springframework.http.HttpStatus;

public class ParkingPlusPaymentNotApprovedException extends SupercashSimpleException {

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
