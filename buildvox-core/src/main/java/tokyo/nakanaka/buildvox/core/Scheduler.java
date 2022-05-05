package tokyo.nakanaka.buildvox.core;

/**
 * A scheduler. This object can schedule a runnable at specified tick from invoking time.
 */
public interface Scheduler {
    /**
     * Schedules runnable at specified tick from the invoking time
     * @param runnable the runnable to execute
     * @param tick the tick time from the invoking time
     * @throws IllegalArgumentException if tick is less than 0.
     */
    void schedule(Runnable runnable, int tick);
}
