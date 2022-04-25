package archware.utils.timers;

import org.apache.commons.lang3.RandomUtils;

public class Stopwatch {
    private long ms;

    public Stopwatch() {
        this.ms = this.getCurrentMS();
    }

    private long getCurrentMS() {
        return System.currentTimeMillis();
    }

    public final long getElapsedTime() {
        return this.getCurrentMS() - this.ms;
    }

    public final boolean elapsed(final long milliseconds) {
        return this.getCurrentMS() - this.ms > milliseconds;
    }

    public final boolean elapsed(final long time, final boolean reset){
        if (System.currentTimeMillis() - this.ms > time) {
            if (reset) {
                this.reset();
            }
            return true;
        }
        return false;
    }

    public final void reset() {
        this.ms = this.getCurrentMS();
    }

    public static long randomDelay(int minDelay, int maxDelay) {
        return RandomUtils.nextInt(minDelay, maxDelay);
    }

    public static long randomClickDelay(int minCPS, int maxCPS) {
        return (long)(Math.random() * (1000 / minCPS - 1000 / maxCPS + 1) + (1000 / maxCPS));
    }
}

