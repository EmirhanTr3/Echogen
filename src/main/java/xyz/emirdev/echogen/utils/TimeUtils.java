package xyz.emirdev.echogen.utils;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtils {

    public static Duration convertStringToDuration(String durationString) {
        if (durationString == null || durationString.trim().isEmpty())
            return null;

        long totalSeconds = 0;
        Pattern pattern = Pattern.compile("(\\d+)\\s*(y|mo|w|d|h|m|s)");
        Matcher matcher = pattern.matcher(durationString.toLowerCase());

        boolean foundAnyMatch = false;

        while (matcher.find()) {
            foundAnyMatch = true;
            long value;
            try {
                value = Long.parseLong(matcher.group(1));
            } catch (NumberFormatException e) {
                return null;
            }
            String unit = matcher.group(2);

            switch (unit) {
                case "y" -> totalSeconds += value * 365L * 24 * 60 * 60; // 365 days per year
                case "mo" -> totalSeconds += value * 30L * 24 * 60 * 60; // 30 days per month
                case "w" -> totalSeconds += value * 7L * 24 * 60 * 60; // 7 days per week
                case "d" -> totalSeconds += value * 24L * 60 * 60;
                case "h" -> totalSeconds += value * 60L * 60;
                case "m" -> totalSeconds += value * 60L;
                case "s" -> totalSeconds += value;
            }
        }

        // Adjusted check for "0s" as the only valid zero duration string
        if (!foundAnyMatch && !durationString.trim().equals("0s"))
            return null;

        return Duration.ofSeconds(totalSeconds);
    }

    public static String parseDurationToString(Duration duration) {
        if (duration == null || duration.isNegative())
            return null;
        if (duration.isZero())
            return "0 seconds";

        long totalSeconds = duration.getSeconds();

        long seconds = totalSeconds % 60;
        long totalMinutes = totalSeconds / 60;
        long minutes = totalMinutes % 60;
        long totalHours = totalMinutes / 60;
        long hours = totalHours % 24;
        long days = totalHours / 24;

        StringBuilder result = new StringBuilder();

        if (days > 0)
            result.append(days).append(days == 1 ? " day" : " days");

        if (hours > 0) {
            if (result.length() > 0)
                result.append(" ");

            result.append(hours).append(hours == 1 ? " hour" : " hours");
        }

        if (minutes > 0) {
            if (result.length() > 0)
                result.append(" ");

            result.append(minutes).append(minutes == 1 ? " minute" : " minutes");
        }

        if (seconds > 0 || result.length() == 0) {
            if (result.length() > 0)
                result.append(" ");

            result.append(seconds).append(seconds == 1 ? " second" : " seconds");
        }

        return result.toString().trim();
    }
}
