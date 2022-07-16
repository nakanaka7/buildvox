package tokyo.nakanaka.buildvox.core.selection;

import tokyo.nakanaka.buildvox.core.BlockSettingArguments;
import tokyo.nakanaka.buildvox.core.Clipboard;
import tokyo.nakanaka.buildvox.core.block.VoxelBlock;
import tokyo.nakanaka.buildvox.core.clientWorld.*;
import tokyo.nakanaka.buildvox.core.command.mixin.BlockSettingOptions;
import tokyo.nakanaka.buildvox.core.edit.WorldEdits;
import tokyo.nakanaka.buildvox.core.math.region3d.Parallelepiped;
import tokyo.nakanaka.buildvox.core.math.region3d.Region3d;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;

/**
 * Represents block accompanied selection. Forward blocks are the blocks which are created at the same time when
 * the selection is created. Backward blocks are the blocks which were replaced with the forward blocks. For blocks to
 * accompany the selection properly when it is affine transformed, both setForwardBlocks() and setBackwardBlocks() must
 * be called properly. Namely, firstly, call setBackwardBlocks() of the original selection, and secondly, call
 * setForwardBlocks() of the transformed selection. Calling setBackwardBlocks() before setForwardBlocks() will fill with
 * background blocks.
 */
public abstract class BlockSelection extends Selection {
    protected Clipboard backwardClip;
    protected double integrity;
    protected VoxelBlock[] filters;
    protected boolean masked;
    protected BlockSettingArguments args;

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

    /** Sets the integrity. Should be called before setForwardBlocks(). */
    public void setIntegrity(double integrity) {
        this.integrity = integrity;
    }

    /** Sets the masked. Should be called before setForwardBlocks().  */
    public void setMasked(boolean masked) {
        this.masked = masked;
    }

    public void setFilters(VoxelBlock... filters) {
        this.filters = filters;
    }

    public void setBlockSettingArguments(BlockSettingArguments blockSettingArguments) {
        this.masked = blockSettingArguments.getMasked();
        this.integrity = blockSettingArguments.getIntegrity();
        this.filters = blockSettingArguments.getFilters();
        this.args = blockSettingArguments;
    }

    public void setBlockSettingOptions(BlockSettingOptions options) {
        setBlockSettingArguments(options.getArguments());
    }

    /**
     * Set forward blocks.
     * @param playerClientWorld a world to set blocks.
     */
    public void setForwardBlocks(PlayerClientWorld playerClientWorld) {
        Clipboard newBackwardClip = new Clipboard();
        WorldEdits.copy(playerClientWorld, this, Vector3d.ZERO, newBackwardClip);
        VoxelBlock background = playerClientWorld.getPlayer().getBackgroundBlock();
        ClientWorld clientWorld = new OptionalClientWorld.Builder(playerClientWorld, background)
                .integrity(integrity)
                .masked(masked)
                .filters(filters)
                .build();
        setRawForwardBlocks(clientWorld);
        backwardClip = newBackwardClip;
    }

    /**
     * Set forward blocks with integrity = 1 and masked = false.
     */
    void setRawForwardBlocks(ClientWorld clientWorld) {

    }

    /**
     * Set backward blocks. The backward blocks are the blocks which were replaced by the last setForwardBlocks().
     * If no calling setForwardBlocks() before, this sets background block into this selection.
     * @param playerClientWorld a world to set blocks.
     */
    public void setBackwardBlocks(PlayerClientWorld playerClientWorld) {
        if(backwardClip == null) {
            WorldEdits.fill(playerClientWorld, this, playerClientWorld.getPlayer().getBackgroundBlock());
        }else {
            WorldEdits.paste(backwardClip, playerClientWorld, Vector3d.ZERO);
        }
    }

}
