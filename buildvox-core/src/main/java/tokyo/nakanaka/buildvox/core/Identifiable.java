package tokyo.nakanaka.buildvox.core;

/**
 * The object which has an id
 * @param <I> the id type
 */
public interface Identifiable<I> {
    /**
     * Get the id
     * @return the id
     */
    I getId();
}
