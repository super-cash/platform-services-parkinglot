package cash.super_.platform.error.supercash;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SupercashErrorCode {
    NO_ERROR(0, "Success"),
    STATUS_NOT_EXPECTED(1001, "Transaction status not expected"),
    SALE_NOT_FOUND(2001, "Sale not found"),
    INVALID_SALE(2002, "Sale not valid"),
    PAYMENT_NOT_APPROVED(3001, "Payment not approved"),
    GENERAL_ERROR(9001, "General error"),
    THIRD_PARTY_EXCEPTION(9998, "Third-party system exception"),
    INVALID_VALUE(9999, "Invalid value");

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
