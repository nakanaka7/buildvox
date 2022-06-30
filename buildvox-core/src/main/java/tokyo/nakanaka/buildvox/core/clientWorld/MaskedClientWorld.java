package tokyo.nakanaka.buildvox.core.clientWorld;

import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.world.VoxelBlock;

/**
 * The client world which skips background block settings.
 */
public class MaskedClientWorld extends ClientWorld {
    private final VoxelBlock background;
    private final ClientWorld cw;

    /**
     * Creates a new instance.
     * @param background the background block.
     * @param original the original client world.
     */
    public MaskedClientWorld(VoxelBlock background, ClientWorld original) {
        super(original.world, original.physics);
        this.background = background;
        this.cw = original;
    }

    @Override
    public void setBlock(Vector3i pos, VoxelBlock block) {
        if(!block.equals(background)) {
            cw.setBlock(pos, block);
        }
    }

}
