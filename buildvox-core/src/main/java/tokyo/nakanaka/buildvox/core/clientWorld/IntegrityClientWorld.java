package tokyo.nakanaka.buildvox.core.clientWorld;

import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.world.VoxelBlock;
import tokyo.nakanaka.buildvox.core.world.World;

/**
 * The client world which changes the integrity of block settings.
 * {@link <a href="https://www.computerhope.com/jargon/i/integrit.htm">integrity</a>}
 */
public class IntegrityClientWorld extends ClientWorld {
    private final double integrity;
    private final ClientWorld clientWorld;

    /**
     * Creates a new instance.
     * @param integrity the integrity of block settings.
     * @param world the original world.
     * @param physics the block setting physics.
     * @throws IllegalArgumentException if the integrity is less than 0 or larger than 1.
     */
    public IntegrityClientWorld(double integrity, World world, boolean physics) {
        this(integrity, new ClientWorld(world, physics));
    }

    /**
     * Creates a new instance.
     * @param integrity integrity the integrity of block settings.
     * @param clientWorld the original client world.
     * @throws IllegalArgumentException if the integrity is less than 0 or larger than 1.
     */
    public IntegrityClientWorld(double integrity, ClientWorld clientWorld) {
        super(clientWorld.original, clientWorld.physics);
        if(integrity < 0 || 1 < integrity)throw new IllegalArgumentException();
        this.integrity = integrity;
        this.clientWorld = clientWorld;
    }

    @Override
    public void setBlock(Vector3i pos, VoxelBlock block) {
        if(Math.random() < integrity) {
            clientWorld.setBlock(pos, block);
        }
    }

}
