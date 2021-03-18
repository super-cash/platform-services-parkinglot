package cash.super_.platform.utils;

public class IsNumber {


    /* TODO: this is one of the worst way to check if a string is a number. There are better alternatives.
       https://www.baeldung.com/java-check-string-number
     */
    public static boolean stringIsLong(String numberStr) {
        if (numberStr != null) {
            try {
                double d = Long.parseLong(numberStr);
                return true;
            } catch (NumberFormatException nfe) {
                return false;
            }
        }
        return false;
    }

    public static boolean stringIsLongWithException(String numberStr, String fieldName) throws IllegalArgumentException {
        if (!IsNumber.stringIsLong(numberStr)) throw new IllegalArgumentException("Field '" + fieldName +
                "' must be a number.");
        return true;
    }
}
