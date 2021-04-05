package cash.super_.platform.utils;

import cash.super_.platform.error.supercash.SupercashInvalidValueException;
import cash.super_.platform.error.supercash.SupercashMissingArgumentException;

public class IsNumber {


    /* TODO: this is one of the worst way to check if a string is a number. There are better alternatives.
       https://www.baeldung.com/java-check-string-number
     */
    public static Double stringIsDouble(String numberStr) {
        try {
            return Double.valueOf(numberStr);
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    public static Double stringIsDoubleWithException(String numberStr, String fieldName) {
        Double number = IsNumber.stringIsDouble(numberStr);
        if (number == null) {
            throw new SupercashInvalidValueException("Field '" + fieldName + "' is not a number.");
        }
        return number;
    }

    public static Long stringIsLong(String numberStr) {
        try {
            return Long.valueOf(numberStr);
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    public static Long stringIsLongWithException(String numberStr, String fieldName) {
        Long number = IsNumber.stringIsLong(numberStr);
        if (number == null) {
            throw new SupercashInvalidValueException("Field '" + fieldName + "' is not a number.");
        }
        return number;
    }
}
