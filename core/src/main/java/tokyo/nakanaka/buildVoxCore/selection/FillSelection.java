package tokyo.nakanaka.buildVoxCore.selection;

import tokyo.nakanaka.buildVoxCore.edit.Clipboard;
import tokyo.nakanaka.buildVoxCore.edit.WorldEdits;
import tokyo.nakanaka.buildVoxCore.editWorld.EditWorld;
import tokyo.nakanaka.buildVoxCore.math.region3d.Parallelepiped;
import tokyo.nakanaka.buildVoxCore.math.region3d.Region3d;
import tokyo.nakanaka.buildVoxCore.math.transformation.AffineTransformation3d;
import tokyo.nakanaka.buildVoxCore.math.vector.Vector3d;
import tokyo.nakanaka.buildVoxCore.world.Block;

public class FillSelection extends BlockSelection {
    private Block block;
    private double integrity;
    private Clipboard backwardClip = new Clipboard();

    public FillSelection(Region3d region3d, Parallelepiped bound, Block block, double integrity) {
        super(region3d, bound);
        this.block = block;
        this.integrity = integrity;
    }

    @Override
    public void setForwardBlocks(EditWorld editWorld) {
        Clipboard newBackwardClip = new Clipboard();
        WorldEdits.copy(editWorld, this, Vector3d.ZERO, newBackwardClip);
        WorldEdits.fill(editWorld, this, block, integrity);
        backwardClip = newBackwardClip;
    }

    @Override
    public void setBackwardBlocks(EditWorld editWorld) {
        WorldEdits.paste(backwardClip, editWorld, Vector3d.ZERO);
    }

    @Override
    public BlockSelection affineTransform(AffineTransformation3d trans) {
        Region3d region3d = super.getRegion3d();
        Parallelepiped bound = super.getBound();
        Selection selection = new Selection(region3d, bound);
        Selection transSelection = selection.affineTransform(trans);
        return new FillSelection(transSelection.getRegion3d(), transSelection.getBound(), block, integrity);
    }

}
