package tokyo.nakanaka.buildvox.core.selection;

import tokyo.nakanaka.buildvox.core.edit.editWorld.EditWorld;
import tokyo.nakanaka.buildvox.core.math.region3d.Parallelepiped;
import tokyo.nakanaka.buildvox.core.math.region3d.Region3d;
import tokyo.nakanaka.buildvox.core.math.transformation.AffineTransformation3d;

/**
 * Represents block accompanied selection. Forward blocks are the blocks which are created at the same time when
 * the selection is created. Backward blocks are the blocks which were replaced with the forward blocks. For blocks to
 * accompany the selection properly when it is affine transformed, both setForwardBlocks() and setBackwardBlocks() must
 * be called properly. Namely, firstly, call setBackwardBlocks() of the original selection, and secondly, call
 * setForwardBlocks() of the transformed selection.
 */
public abstract class BlockSelection extends Selection {
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
     * @param editWorld a world to set blocks.
     */
    public abstract void setForwardBlocks(EditWorld editWorld);

    /**
     * Set backward blocks.
     * @param editWorld a world to set blocks.
     */
    public abstract void setBackwardBlocks(EditWorld editWorld);
    @Override
    public abstract BlockSelection affineTransform(AffineTransformation3d trans);

}
