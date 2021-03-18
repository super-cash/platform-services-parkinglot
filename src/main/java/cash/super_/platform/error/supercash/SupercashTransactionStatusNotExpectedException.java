package cash.super_.platform.error.supercash;

import cash.super_.platform.error.supercash.SupercashErrorCode;
import cash.super_.platform.error.supercash.SupercashException;
import cash.super_.platform.service.pagarme.transactions.models.Transaction;
import org.springframework.http.HttpStatus;

public class SupercashTransactionStatusNotExpectedException extends SupercashException {

    private static final String MESSAGE_FORMAT = "Transaction status '%s' expected, but '%s' found.";

    public SupercashTransactionStatusNotExpectedException() {
        super(SupercashErrorCode.STATUS_NOT_EXPECTED, HttpStatus.BAD_REQUEST);
    }

    public SupercashTransactionStatusNotExpectedException(Transaction.Status expectedStatus,
                                                          Transaction.Status foundStatus) {
        super(SupercashErrorCode.STATUS_NOT_EXPECTED, HttpStatus.BAD_REQUEST,
                String.format(MESSAGE_FORMAT, expectedStatus, foundStatus));
    }

    public SupercashTransactionStatusNotExpectedException(Transaction.Status expectedStatus,
                                                          Transaction.Status foundStatus,
                                                          HttpStatus additionalErrorCode) {
        super(SupercashErrorCode.STATUS_NOT_EXPECTED, additionalErrorCode, String.format(MESSAGE_FORMAT,
                expectedStatus, foundStatus));
    }
}
