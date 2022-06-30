package tokyo.nakanaka.buildvox.core.selection;

import tokyo.nakanaka.buildvox.core.Clipboard;
import tokyo.nakanaka.buildvox.core.clientWorld.ClientWorld;
import tokyo.nakanaka.buildvox.core.clientWorld.IntegrityClientWorld;
import tokyo.nakanaka.buildvox.core.clientWorld.MaskedClientWorld;
import tokyo.nakanaka.buildvox.core.clientWorld.PlayerWorld;
import tokyo.nakanaka.buildvox.core.edit.WorldEdits;
import tokyo.nakanaka.buildvox.core.math.region3d.Parallelepiped;
import tokyo.nakanaka.buildvox.core.math.region3d.Region3d;
import tokyo.nakanaka.buildvox.core.math.transformation.AffineTransformation3d;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;
import tokyo.nakanaka.buildvox.core.world.VoxelBlock;

/**
 * Represents block accompanied selection. Forward blocks are the blocks which are created at the same time when
 * the selection is created. Backward blocks are the blocks which were replaced with the forward blocks. For blocks to
 * accompany the selection properly when it is affine transformed, both setForwardBlocks() and setBackwardBlocks() must
 * be called properly. Namely, firstly, call setBackwardBlocks() of the original selection, and secondly, call
 * setForwardBlocks() of the transformed selection.
 */
public abstract class BlockSelection extends Selection {
    protected Clipboard backwardClip = new Clipboard();
    protected double integrity;
    protected boolean masked;
    protected VoxelBlock background;

    public BlockSelection(Region3d region3d, Parallelepiped bound) {
        super(region3d, bound);
    }

    /**
     * Converts this instance to a {@link Selection} instance
     * @return a {@link Selection} instance
     */
    public Selection toNonBlock() {
        return new Selection(getRegion3d(), getBound());
    }

    /**
     * Set forward blocks.
     * @param playerWorld a world to set blocks.
     */
    public void setForwardBlocks(PlayerWorld playerWorld) {
        Clipboard newBackwardClip = new Clipboard();
        WorldEdits.copy(playerWorld, this, Vector3d.ZERO, newBackwardClip);
        ClientWorld clientWorld = new IntegrityClientWorld(integrity, playerWorld);
        if(masked) {
            clientWorld = new MaskedClientWorld(background, playerWorld);
        }
        setRawForwardBlocks(clientWorld);
        backwardClip = newBackwardClip;
    }

    /**
     * Set forward blocks with integrity = 1 and masked = false.
     */
    void setRawForwardBlocks(ClientWorld clientWorld) {

    }

    /**
     * Set backward blocks.
     * @param playerWorld a world to set blocks.
     */
    public void setBackwardBlocks(PlayerWorld playerWorld) {
        WorldEdits.paste(backwardClip, playerWorld, Vector3d.ZERO);
    }

    @Override
    public abstract BlockSelection affineTransform(AffineTransformation3d trans);

}
