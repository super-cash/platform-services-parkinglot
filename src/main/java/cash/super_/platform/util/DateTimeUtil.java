package cash.super_.platform.util;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.TimeZone;

/**
 * The specific date and time calculations for the apps
 * Verify at https://currentmillis.com/
 */
public enum DateTimeUtil {

    ;

    public static final String TIMEZONE_AMERICA_SAO_PAULO = "America/Sao_Paulo";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss a z")
            .withZone(TimeZone.getTimeZone(TIMEZONE_AMERICA_SAO_PAULO).toZoneId());

    public static long getMillis(LocalDateTime dateTime) {
        return dateTime.atZone(TimeZone.getTimeZone(TIMEZONE_AMERICA_SAO_PAULO).toZoneId()).toInstant().toEpochMilli();
    }

    public static String getFormatted(long millis) {
        return getLocalDateTime(millis).format(DATE_TIME_FORMATTER);
    }

    public static LocalDateTime getLocalDateTime(long milliseconds) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), TimeZone.getDefault().toZoneId());
        return dateTime.atZone(ZoneOffset.UTC)
                .withZoneSameInstant(ZoneId.of(TIMEZONE_AMERICA_SAO_PAULO))
                .toLocalDateTime();
    }

    public static long convertToTimezone(long millis) {
        return DateTimeUtil.getMillis(DateTimeUtil.getLocalDateTime(millis));
    }

    /**
     * Correctly converts the time to Brazil time
     * @param dateTime
     * @return It converts correctly
     */
    public static LocalDateTime convertToTimezone(LocalDateTime dateTime) {
        return dateTime.atZone(TimeZone.getTimeZone(TIMEZONE_AMERICA_SAO_PAULO).toZoneId()).toLocalDateTime();
    }

    /**
     * @return Get the local
     */
    public static LocalDateTime getNowLocalDateTime() {
        long now = getNow();
        return getLocalDateTime(now);
    }

    /**
     * @return The current time at the given timezone
     */
    public static long getNow() {
        return getMillis(LocalDateTime.now());
    }

    /**
     * @param dateTime
     * @return The start and end hour of the day for searches
     */
    public static Long[] getDateZeroHoraMidnightInterval(LocalDateTime dateTime) {
        // https://stackoverflow.com/questions/9629636/get-todays-date-in-java-at-midnight-time/31683549#31683549
        // http://groovyconsole.appspot.com/script/5116630166142976
        LocalDateTime earlyMorning = dateTime.toLocalDate().atStartOfDay();
        LocalDateTime lateNight = earlyMorning.plusHours(23).plusMinutes(59);
        return Arrays.asList(getMillis(earlyMorning), getMillis(lateNight)).toArray(new Long[2]);
    }
}
