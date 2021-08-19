package cash.super_.platform.error.parkinglot;

import cash.super_.platform.adapter.feign.SupercashErrorCode;
import cash.super_.platform.adapter.feign.SupercashSimpleException;
import cash.super_.platform.model.payment.pagarme.Transaction;
import org.springframework.http.HttpStatus;

public class SupercashTransactionStatusNotExpectedException extends SupercashSimpleException {

    private static final String MESSAGE_FORMAT = "Transaction status '%s' expected, but '%s' found.";

    public SupercashTransactionStatusNotExpectedException() {
        super(SupercashErrorCode.STATUS_NOT_EXPECTED, HttpStatus.BAD_REQUEST);
    }

    public SupercashTransactionStatusNotExpectedException(Transaction.Status expectedStatus,
                                                          Transaction.Status returnedStatus) {
        super(SupercashErrorCode.STATUS_NOT_EXPECTED, HttpStatus.BAD_REQUEST,
                String.format(MESSAGE_FORMAT, expectedStatus, returnedStatus));
        super.SupercashExceptionModel.addField("expected_status", expectedStatus);
        super.SupercashExceptionModel.addField("returned_status", returnedStatus);
    }

    public SupercashTransactionStatusNotExpectedException(Transaction.Status expectedStatus,
                                                          Transaction.Status returnedStatus,
                                                          HttpStatus additionalErrorCode) {
        super(SupercashErrorCode.STATUS_NOT_EXPECTED, additionalErrorCode, String.format(MESSAGE_FORMAT,
                expectedStatus, returnedStatus));
        super.SupercashExceptionModel.addField("expected_status", expectedStatus);
        super.SupercashExceptionModel.addField("returned_status", returnedStatus);
    }
}
