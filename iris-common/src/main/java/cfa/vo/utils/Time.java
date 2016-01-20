package cfa.vo.utils;

import java.util.concurrent.TimeUnit;

/**
 * Class for storing timeout values as amount and time unit.
 */
public class Time {
    private long amount;
    private TimeUnit unit;

    /**
     * Construct a Time
     * @param amount the amount of time
     * @param unit the unit of time
     */
    public Time(long amount, TimeUnit unit) {
        this.amount = amount;
        this.unit = unit;
    }

    /**
     * Get unit
     * @return the time unit
     */
    public TimeUnit getUnit() {
        return unit;
    }

    /**
     * Get amount
     * @return the time amount in this timeout
     */
    public long getAmount() {
        return amount;
    }

    /**
     * Convert this instance to a different unit
     * @param unit the TimeUnit to convert to
     * @return a new Time instance
     */
    public Time convertTo(TimeUnit unit) {
        return new Time(unit.convert(this.amount, this.unit), unit);
    }
}
