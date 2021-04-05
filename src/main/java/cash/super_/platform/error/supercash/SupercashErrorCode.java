package cash.super_.platform.error.supercash;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SupercashErrorCode {
    NO_ERROR(0, "Success"),
    STATUS_NOT_EXPECTED(1001, "Transaction status not expected."),
    SALE_NOT_FOUND(2001, "Sale not found."),
    INVALID_SALE(2002, "Sale not valid."),
    MARKETPLACE_NOT_FOUND(2003, "Marketplace not found."),
    PAYMENT_NOT_APPROVED(3001, "Payment not approved."),
    AMOUNT_IS_ZERO(3002, "The ticket fee is 0."),
    ALREADY_PAID(3003, "This payment is already processed."),
    WRONG_CLIENT_VERSION(4001, "The client version is wrong."),
    GENERIC_ERROR(9001, "Generic error."),
    UNKNOWN_HOST(9996, "Unknown Host Exception."),
    MISSING_ARGUMENT(9997, "Missing argument."),
    THIRD_PARTY_EXCEPTION(9998, "Third-party system exception."),
    INVALID_VALUE(9999, "Invalid value.");

    private final int value;

    private String description;

    SupercashErrorCode(int value, String description) {
        this.value = value;
        this.description = description;
    }

    @JsonValue
    public int value() {
        return value;
    }

    public static SupercashErrorCode valueOf(int value) {
        SupercashErrorCode errorCode = resolve(value);
        if (errorCode == null) {
            throw new IllegalArgumentException("No matching constant for [" + value + "]");
        }
        return errorCode;
    }

    public String description() {
        return description;
    }

    public static SupercashErrorCode resolve(int value) {
        for (SupercashErrorCode errorCode : values()) {
            if (errorCode.value == value) {
                return errorCode;
            }
        }
        return null;
    }
}
