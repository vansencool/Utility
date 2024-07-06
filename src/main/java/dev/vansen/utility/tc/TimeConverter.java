package dev.vansen.utility.tc;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class TimeConverter {

    public static long convertToMillis(@NotNull String timeUnit, long duration) {
        String upperCaseTimeUnit = timeUnit.toUpperCase();
        return switch (upperCaseTimeUnit) {
            case "NA", "NANOSECOND" -> TimeUnit.NANOSECONDS.toMillis(duration);
            case "MI", "MILLISECOND" -> duration;
            case "S", "SEC", "SECOND" -> TimeUnit.SECONDS.toMillis(duration);
            case "M", "MINUTE" -> TimeUnit.MINUTES.toMillis(duration);
            case "H", "HOUR" -> TimeUnit.HOURS.toMillis(duration);
            case "D", "DAY" -> TimeUnit.DAYS.toMillis(duration);
            case "MO", "MONTH" -> TimeUnit.DAYS.toMillis(duration * 30);
            case "Y", "YEAR" -> TimeUnit.DAYS.toMillis(duration * 365);
            default -> throw new IllegalArgumentException("Invalid time unit");
        };
    }
}