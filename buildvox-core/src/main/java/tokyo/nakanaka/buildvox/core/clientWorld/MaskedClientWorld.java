package tokyo.nakanaka.buildvox.core.clientWorld;

import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.world.VoxelBlock;
import tokyo.nakanaka.buildvox.core.world.World;

/**
 * The client world which skips background block settings.
 */
public class MaskedClientWorld extends ClientWorld {
    private final VoxelBlock background;
    private final ClientWorld cw;

    /**
     * Creates a new instance.
     * @param background the background block.
     * @param original the original world.
     * @param physics the block setting physics.
     */
    public MaskedClientWorld(VoxelBlock background, World original, boolean physics) {
        this(background, new ClientWorld(original, physics));
    }

    /**
     * Creates a new instance.
     * @param background the background block.
     * @param original the original client world.
     */
    public MaskedClientWorld(VoxelBlock background, ClientWorld original) {
        super(original.original, original.physics);
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
