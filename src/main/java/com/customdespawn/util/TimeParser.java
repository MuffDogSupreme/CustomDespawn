package com.customdespawn.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeParser {

    private static final Pattern DURATION_PATTERN = Pattern.compile("(\\d+)([sm])?");
    private static final int TICKS_PER_SECOND = 20;
    private static final int SECONDS_PER_MINUTE = 60;

    public static long parseDurationStringToTicks(String durationString) {
        if (durationString == null || durationString.trim().isEmpty()) {
            throw new IllegalArgumentException("Duration string cannot be null or empty.");
        }

        Matcher matcher = DURATION_PATTERN.matcher(durationString.trim().toLowerCase());

        if (matcher.matches()) {
            long value = Long.parseLong(matcher.group(1));
            String unit = matcher.group(2);

            if ("s".equals(unit)) {
                return value * TICKS_PER_SECOND;
            } else if ("m".equals(unit)) {
                return value * SECONDS_PER_MINUTE * TICKS_PER_SECOND;
            }

            return value;
        }

        try {
            return Long.parseLong(durationString.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid duration string format: " + durationString);
        }
    }
}