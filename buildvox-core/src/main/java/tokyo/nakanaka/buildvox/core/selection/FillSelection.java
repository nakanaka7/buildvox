package tokyo.nakanaka.buildvox.core.selection;

import tokyo.nakanaka.buildvox.core.clientWorld.ClientWorld;
import tokyo.nakanaka.buildvox.core.edit.WorldEdits;
import tokyo.nakanaka.buildvox.core.math.region3d.Parallelepiped;
import tokyo.nakanaka.buildvox.core.math.region3d.Region3d;
import tokyo.nakanaka.buildvox.core.math.transformation.AffineTransformation3d;
import tokyo.nakanaka.buildvox.core.block.VoxelBlock;

/**
 * Represents the selection when filling.
 */
public class FillSelection extends BlockSelection {
    private final VoxelBlock block;

    private FillSelection(Region3d region3d, Parallelepiped bound, VoxelBlock block) {
        super(region3d, bound);
        this.block = block;
    }

    /**
     * The builder class of FillSelection.
     */
    public static class Builder {
        private final VoxelBlock block;
        private final Selection sel;
        private double integrity = 1;
        private boolean masked;

        /**
         * Creates a new instance.
         * @param block the block to set.
         * @param sel the original selection.
         */
        public Builder(VoxelBlock block, Selection sel) {
            this.block = block;
            this.sel = sel;
        }

        /**
         * Sets the integrity.
         */
        public Builder integrity(double integrity) {
            this.integrity = integrity;
            return this;
        }

        /**
         * Sets masked.
         */
        public Builder masked(boolean masked) {
            this.masked = masked;
            return this;
        }

        /**
         * Builds a new instance.
         * @return a new instance.
         */
        public FillSelection build() {
            var i = new FillSelection(sel.getRegion3d(), sel.getBound(), block);
            i.integrity = this.integrity;
            i.masked = this.masked;
            return i;
        }

    }

    void setRawForwardBlocks(ClientWorld clientWorld) {
        WorldEdits.fill(clientWorld, this, block);
    }

    @Override
    public BlockSelection affineTransform(AffineTransformation3d trans) {
        Region3d region3d = super.getRegion3d();
        Parallelepiped bound = super.getBound();
        Selection selection = new Selection(region3d, bound);
        Selection transSelection = selection.affineTransform(trans);
        return new Builder(block, transSelection)
                .integrity(this.integrity)
                .masked(this.masked)
                .build();
    }

}
