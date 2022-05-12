package tokyo.nakanaka.buildvox.core.system;

/**
 * The object which has an id
 * @param <I> the id type
 */
public interface Entity<I> {
    /**
     * Get the id
     * @return the id
     */
    I getId();
}
