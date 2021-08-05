package cash.super_.platform.utils;

import cash.super_.platform.error.supercash.SupercashInvalidValueException;
import cash.super_.platform.error.supercash.SupercashMissingArgumentException;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Validator of numbers using faster validation with apache commons.
 * *** Performance considerations: https://www.baeldung.com/java-check-string-number
 */
public class NumberUtil {

    /**
     * The pattern to validate if a number is a float number
     */
    public static final Pattern FLOAT_NUMBER_PATTERN = Pattern.compile("-?\\d+(\\.\\d+)");

    /**
     * @param numberStr is the string
     * @return Whether the given number is a double
     */
    public static Optional<Double> stringIsDouble(String numberStr) {
        // https://stackoverflow.com/questions/58772425/double-to-string-alternative-due-to-performance-issue/58772545#58772545
        return FLOAT_NUMBER_PATTERN.matcher(numberStr).matches() ? Optional.of(Double.valueOf(numberStr)) : null;
    }

    /**
     * @param numberStr is the string
     * @return Whether the given number is a long
     */
    public static Optional<Long> stringIsLong(String numberStr) {
        // https://www.baeldung.com/java-check-string-number#benchmark-enhanced
        return StringUtils.isNumeric(numberStr) ? Optional.of(Long.valueOf(numberStr)) : null;
    }

    public static Double stringIsDoubleWithException(String numberStr, String fieldName) {
        if (Strings.isNullOrEmpty(numberStr)) {
            throw new SupercashMissingArgumentException("Field '" + fieldName + "' is required.");
        }
        Optional<Double> number = stringIsDouble(numberStr);
        if (number == null) {
            throw new SupercashMissingArgumentException("Field '" + fieldName + "' is required.");

        } else if (!number.isPresent()) {
            throw new SupercashInvalidValueException("Field '" + fieldName + "' is not a number.");
        }
        return number.get();
    }

    public static Long stringIsLongWithException(String numberStr, String fieldName) {
        Optional<Long> number = stringIsLong(numberStr);
        if (number == null) {
            throw new SupercashMissingArgumentException("Field '" + fieldName + "' is required.");

        } else  if (!number.isPresent()) {
            throw new SupercashInvalidValueException("Field '" + fieldName + "' is not a number.");
        }
        return number.get();
    }
}
