package dev.vansen.utility.tc;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class TimeConverter {

    public static long convertToMillis(@NotNull String timeUnit, long duration) {
        String upperCaseTimeUnit = timeUnit.toUpperCase();
        return switch (upperCaseTimeUnit) {
            case "NA", "NANOSECOND", "NANOSECONDS" -> TimeUnit.NANOSECONDS.toMillis(duration);
            case "MI", "MILLISECOND", "MILLISECONDS" -> duration;
            case "S", "SEC", "SECOND", "SECONDS" -> TimeUnit.SECONDS.toMillis(duration);
            case "M", "MINUTE", "MINUTES" -> TimeUnit.MINUTES.toMillis(duration);
            case "H", "HOUR", "HOURS" -> TimeUnit.HOURS.toMillis(duration);
            case "D", "DAY", "DAYS" -> TimeUnit.DAYS.toMillis(duration);
            case "MO", "MONTH", "MONTHS" -> TimeUnit.DAYS.toMillis(duration * 30);
            case "Y", "YEAR", "YEARS" -> TimeUnit.DAYS.toMillis(duration * 365);
            default -> throw new IllegalArgumentException("Invalid time unit");
        };
    }
}