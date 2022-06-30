package tokyo.nakanaka.buildvox.core;

/**
 * Stores an edit exit information.
 *
 * @param blockCount  the block counts(including block entity)
 * @param entityCount the entity counts
 * @param biomeCount  the biome count
 */
public record EditExit(int blockCount, int entityCount, int biomeCount) {
}
