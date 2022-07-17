package tokyo.nakanaka.buildvox.core.selection;

import tokyo.nakanaka.buildvox.core.block.BlockTransformation;
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
    private final AffineTransformation3d totalTrans;

    private FillSelection(Region3d region3d, Parallelepiped bound, VoxelBlock block, AffineTransformation3d totalTrans) {
        super(region3d, bound);
        this.block = block;
        this.totalTrans = totalTrans;
    }

    /**
     * The builder class of FillSelection.
     */
    public static class Builder {
        private final VoxelBlock block;
        private final Selection sel;
        private final AffineTransformation3d totalTrans;
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
            this.totalTrans = AffineTransformation3d.IDENTITY;
        }

        private Builder(VoxelBlock block, Selection sel, AffineTransformation3d totalTrans) {
            this.block = block;
            this.sel = sel;
            this.totalTrans = totalTrans;
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
            var i = new FillSelection(sel.getRegion3d(), sel.getBound(), block, totalTrans);
            i.setIntegrity(this.integrity);
            i.setMasked(this.masked);
            return i;
        }

    }

    void setRawForwardBlocks(ClientWorld clientWorld) {
        BlockTransformation bt = BlockTransformation.approximateOf(totalTrans);
        VoxelBlock transBlock = block.transform(bt);
        WorldEdits.fill(clientWorld, this, transBlock);
    }

    @Override
    public FillSelection affineTransform(AffineTransformation3d trans) {
        Region3d region3d = super.getRegion3d();
        Parallelepiped bound = super.getBound();
        Selection selection = new Selection(region3d, bound);
        Selection transSelection = selection.affineTransform(trans);
        FillSelection newFillSel = new Builder(block, transSelection, totalTrans.compose(trans)).build();
        newFillSel.setOptions(getOptions());
        return newFillSel;
    }

}
