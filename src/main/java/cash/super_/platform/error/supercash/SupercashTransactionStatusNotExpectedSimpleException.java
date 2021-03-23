package cash.super_.platform.error.supercash;

import cash.super_.platform.service.pagarme.transactions.models.Transaction;
import org.springframework.http.HttpStatus;

public class SupercashTransactionStatusNotExpectedSimpleException extends SupercashSimpleException {

    private static final String MESSAGE_FORMAT = "Transaction status '%s' expected, but '%s' found.";

    public SupercashTransactionStatusNotExpectedSimpleException() {
        super(SupercashErrorCode.STATUS_NOT_EXPECTED, HttpStatus.BAD_REQUEST);
    }

    public SupercashTransactionStatusNotExpectedSimpleException(Transaction.Status expectedStatus,
                                                                Transaction.Status returnedStatus) {
        super(SupercashErrorCode.STATUS_NOT_EXPECTED, HttpStatus.BAD_REQUEST,
                String.format(MESSAGE_FORMAT, expectedStatus, returnedStatus));
        super.SupercashExceptionModel.addField("expected_status", expectedStatus);
        super.SupercashExceptionModel.addField("returned_status", returnedStatus);
    }

    public SupercashTransactionStatusNotExpectedSimpleException(Transaction.Status expectedStatus,
                                                                Transaction.Status returnedStatus,
                                                                HttpStatus additionalErrorCode) {
        super(SupercashErrorCode.STATUS_NOT_EXPECTED, additionalErrorCode, String.format(MESSAGE_FORMAT,
                expectedStatus, returnedStatus));
        super.SupercashExceptionModel.addField("expected_status", expectedStatus);
        super.SupercashExceptionModel.addField("returned_status", returnedStatus);
    }
}
