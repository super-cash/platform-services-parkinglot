package cash.super_.platform.utils;

import java.time.*;
import java.util.Arrays;
import java.util.TimeZone;

/**
 * The specific date and time calculations for the apps
 * Verify at https://currentmillis.com/
 */
public enum DateTimeUtil {

    ;

    public static final String TIMEZONE_AMERICA_SAO_PAULO = "America/Sao_Paulo";

    public static long getMillis(LocalDateTime dateTime) {
        // TODO: fix the calculation here
        return dateTime.atZone(TimeZone.getTimeZone(TIMEZONE_AMERICA_SAO_PAULO).toZoneId()).minusHours(3).toInstant().toEpochMilli();
    }

    public static LocalDateTime getLocalDateTime(long milliseconds) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), TimeZone.getDefault().toZoneId());
        return dateTime.atZone(ZoneId.of("UTC"))
                .withZoneSameInstant(ZoneId.of(TIMEZONE_AMERICA_SAO_PAULO))
                .toLocalDateTime();
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
