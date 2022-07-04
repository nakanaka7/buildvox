package tokyo.nakanaka.buildvox.core.block;

/**
 * A functional interface to validate the given block is settable into the world of the platform.
 * Deprecated. Block setting validation should be done when parsing.
 */
@Deprecated()
public interface BlockValidator {
    /**
     * Checks the given block is settable into the world of the platform.
     * @param block a block
     * @return true if the given block is settable into the world of the platform, otherwise false.
     */
    boolean validate(VoxelBlock block);
}
