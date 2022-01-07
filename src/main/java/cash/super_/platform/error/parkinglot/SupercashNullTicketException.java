package cash.super_.platform.error.parkinglot;

import cash.super_.platform.adapter.feign.SupercashErrorCode;
import cash.super_.platform.adapter.feign.SupercashSimpleException;
import org.springframework.http.HttpStatus;

public class SupercashNullTicketException extends SupercashSimpleException {

    public SupercashNullTicketException() {
        super(SupercashErrorCode.PARKING_LOT_TICKET_NULL_STATUS_EXCEPTION, HttpStatus.FAILED_DEPENDENCY);
    }

    public SupercashNullTicketException(HttpStatus additionalErrorCode) {
        super(SupercashErrorCode.PARKING_LOT_TICKET_NULL_STATUS_EXCEPTION, additionalErrorCode);
    }

    public SupercashNullTicketException(String additionalMessage) {
        super(SupercashErrorCode.PARKING_LOT_TICKET_NULL_STATUS_EXCEPTION, HttpStatus.FAILED_DEPENDENCY, additionalMessage);
    }

    public SupercashNullTicketException(HttpStatus additionalErrorCode, String additionalMessage) {
        super(SupercashErrorCode.PARKING_LOT_TICKET_NULL_STATUS_EXCEPTION, additionalErrorCode, additionalMessage);
    }
}
