package com.dunghx.fossil;

import java.util.concurrent.TimeUnit;

public class TimerUtil {

    public static long calculateTimeMilliseconds(long min, long sec) {
        return TimeUnit.MINUTES.toMillis(min) + TimeUnit.SECONDS.toMillis(sec);
    }

    public static int calculatePercent(long timeRemaining, long totalTime) {
        return (int) (((double) timeRemaining / (double) totalTime) * 100);
    }

    public static long calculateMinutesByMilli(long milli) {
        return TimeUnit.MILLISECONDS.toMinutes(milli) % 60;
    }

    public static long calculateSecondsByMilli(long milli) {
        return TimeUnit.MILLISECONDS.toSeconds(milli) % 60;
    }
}
