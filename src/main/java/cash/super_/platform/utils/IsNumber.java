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
            throw new SupercashInvalidValueException("Field '" + fieldName + "' must be provided as a number.");
        }
        return number;
    }
}
