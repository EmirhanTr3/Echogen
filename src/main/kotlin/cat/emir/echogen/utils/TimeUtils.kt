package cat.emir.echogen.utils

import java.time.Duration
import java.util.regex.Pattern

class TimeUtils {
    companion object {
        fun convertStringToDuration(durationString: String?): Duration? {
            if (durationString == null || durationString.trim().isEmpty())
                return null

            var totalSeconds = 0L
            val matcher = Pattern.compile("(\\d+)\\s*(y|mo|w|d|h|m|s)").matcher(durationString.lowercase())

            var foundAnyMatch = false

            while (matcher.find()) {
                foundAnyMatch = true
                val value = matcher.group(1).toLong()
                val unit = matcher.group(2)

                when (unit) {
                    "y" -> totalSeconds += value * 365L * 24 * 60 * 60
                    "mo" -> totalSeconds += value * 30L * 24 * 60 * 60
                    "w" -> totalSeconds += value * 7L * 24 * 60 * 60
                    "d" -> totalSeconds += value * 24L * 60 * 60
                    "h" -> totalSeconds += value * 60L * 60
                    "m" -> totalSeconds += value * 60L
                    "s" -> totalSeconds += value
                }
            }

            if (!foundAnyMatch && durationString.trim() != "0s")
                return null

            return Duration.ofSeconds(totalSeconds)
        }

        fun parseDurationToString(duration: Duration?): String? {
            if (duration == null || duration.isNegative)
                return null
            if (duration.isZero)
                return "0 seconds"

            val totalSeconds = duration.seconds

            val seconds = totalSeconds % 60
            val totalMinutes = totalSeconds / 60
            val minutes = totalMinutes % 60
            val totalHours = totalMinutes / 60
            val hours = totalHours % 24
            val days = totalHours / 24

            val result = StringBuilder()

            if (days > 0)
                result.append(days).append(if (days == 1L) " day" else " days")

            if (hours > 0) {
                if (!result.isEmpty())
                    result.append(" ")

                result.append(hours).append(if (hours == 1L) " hour" else " hours")
            }

            if (minutes > 0) {
                if (!result.isEmpty())
                    result.append(" ")

                result.append(minutes).append(if (minutes == 1L) " minute" else " minutes")
            }

            if (seconds > 0 || result.isEmpty()) {
                if (!result.isEmpty())
                    result.append(" ")

                result.append(seconds).append(if (seconds == 1L) " second" else " seconds")
            }

            return result.toString().trim()
        }
    }
}
