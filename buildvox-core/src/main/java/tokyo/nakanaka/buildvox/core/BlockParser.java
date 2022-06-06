package tokyo.nakanaka.buildvox.core;

import tokyo.nakanaka.buildvox.core.world.VoxelBlock;

/**
 * A functional interface to parse a String to the BlockState.
 */
public interface BlockParser {
    /**
     * Parses the String to a BlockState.
     * @param s the String.
     * @return the BlockState.
     * @throws IllegalArgumentException if s cannot be parsed to a BlockState.
     */
    VoxelBlock parse(String s);
}
