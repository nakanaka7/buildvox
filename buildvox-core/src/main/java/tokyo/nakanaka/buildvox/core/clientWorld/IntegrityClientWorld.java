package tokyo.nakanaka.buildvox.core.clientWorld;

import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.block.VoxelBlock;

/**
 * The client world which changes the integrity of block settings.
 * {@link <a href="https://www.computerhope.com/jargon/i/integrit.htm">integrity</a>}
 */
public class IntegrityClientWorld extends ClientWorld {
    private final double integrity;
    private final ClientWorld clientWorld;
    private final VoxelBlock replace;

    /**
     * Creates a new instance.
     * @param integrity integrity the integrity of block settings.
     * @param clientWorld the original client world.
     * @throws IllegalArgumentException if the integrity is less than 0 or larger than 1.
     */
    public IntegrityClientWorld(double integrity, ClientWorld clientWorld) {
        this(integrity, null, clientWorld);
    }

    /**
     * Creates a new instance.
     * @param integrity integrity the integrity of block settings.
     * @param replace the replacing block when the original block setting was skipped.
     * @param clientWorld the original client world.
     * @throws IllegalArgumentException if the integrity is less than 0 or larger than 1.
     */
    public IntegrityClientWorld(double integrity, VoxelBlock replace, ClientWorld clientWorld) {
        super(clientWorld.getWorld(), clientWorld.getPhysics());
        if(integrity < 0 || 1 < integrity)throw new IllegalArgumentException();
        this.integrity = integrity;
        this.clientWorld = clientWorld;
        this.replace = replace;
    }

    @Override
    public void setBlock(Vector3i pos, VoxelBlock block) {
        if(Math.random() < integrity) {
            clientWorld.setBlock(pos, block);
        }else{
            if(replace != null) {
                clientWorld.setBlock(pos, replace);
            }
        }

    }

}
