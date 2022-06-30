package tokyo.nakanaka.buildvox.core.selection;

import tokyo.nakanaka.buildvox.core.Clipboard;
import tokyo.nakanaka.buildvox.core.edit.WorldEdits;
import tokyo.nakanaka.buildvox.core.clientWorld.ClientWorld;
import tokyo.nakanaka.buildvox.core.clientWorld.IntegrityClientWorld;
import tokyo.nakanaka.buildvox.core.math.region3d.Parallelepiped;
import tokyo.nakanaka.buildvox.core.math.region3d.Region3d;
import tokyo.nakanaka.buildvox.core.math.transformation.AffineTransformation3d;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;
import tokyo.nakanaka.buildvox.core.world.VoxelBlock;

public class FillSelection extends BlockSelection {
    private VoxelBlock block;
    private double integrity;
    private Clipboard backwardClip = new Clipboard();

    public FillSelection(Region3d region3d, Parallelepiped bound, VoxelBlock block, double integrity) {
        super(region3d, bound);
        this.block = block;
        this.integrity = integrity;
    }

    @Override
    public void setForwardBlocks(ClientWorld clientWorld) {
        Clipboard newBackwardClip = new Clipboard();
        WorldEdits.copy(clientWorld, this, Vector3d.ZERO, newBackwardClip);
        clientWorld = new IntegrityClientWorld(integrity, clientWorld);
        WorldEdits.fill(clientWorld, this, block);
        backwardClip = newBackwardClip;
    }

    @Override
    public void setBackwardBlocks(ClientWorld clientWorld) {
        WorldEdits.paste(backwardClip, clientWorld, Vector3d.ZERO);
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
