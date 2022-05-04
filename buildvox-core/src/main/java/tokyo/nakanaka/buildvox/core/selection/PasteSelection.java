package tokyo.nakanaka.buildvox.core.selection;

import tokyo.nakanaka.buildvox.core.edit.Clipboard;
import tokyo.nakanaka.buildvox.core.edit.WorldEdits;
import tokyo.nakanaka.buildvox.core.editWorld.EditWorld;
import tokyo.nakanaka.buildvox.core.math.region3d.Cuboid;
import tokyo.nakanaka.buildvox.core.math.region3d.Parallelepiped;
import tokyo.nakanaka.buildvox.core.math.region3d.Region3d;
import tokyo.nakanaka.buildvox.core.math.transformation.AffineTransformation3d;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;

public class PasteSelection extends BlockSelection {
    private Clipboard clipboard;
    private Vector3d offset;
    private AffineTransformation3d clipTrans;
    private double integrity;
    private Clipboard backwardClip = new Clipboard();

    private PasteSelection(Region3d region3d, Parallelepiped bound, Clipboard clipboard, Vector3d offset,
                           AffineTransformation3d clipTrans) {
        super(region3d, bound);
        this.clipboard = clipboard;
        this.offset = offset;
        this.clipTrans = clipTrans;
    }

    private PasteSelection(Region3d region3d, Parallelepiped bound, Clipboard clipboard, Vector3d offset,
                           AffineTransformation3d clipTrans, double integrity) {
        this(region3d, bound, clipboard, offset, clipTrans);
        this.integrity = integrity;
    }

    public static PasteSelection newInstance(Clipboard clipboard, Vector3d offset, AffineTransformation3d clipTrans) {
        return newInstance(clipboard, offset, clipTrans, 1);
    }

    public static PasteSelection newInstance(Clipboard clipboard, Vector3d offset, AffineTransformation3d clipTrans, double integrity) {
        double maxX = clipboard.maxX() + 1;
        double maxY = clipboard.maxY() + 1;
        double maxZ = clipboard.maxZ() + 1;
        double minX = clipboard.minX();
        double minY = clipboard.minY();
        double minZ = clipboard.minZ();
        Cuboid cuboid = new Cuboid(maxX, maxY, maxZ, minX, minY, minZ);
        var bound = new Parallelepiped(cuboid.x1(), cuboid.y1(), cuboid.z1(), cuboid.x2(), cuboid.y2(), cuboid.z2());
        var selection = new Selection(cuboid, bound)
                .affineTransform(clipTrans)
                .translate(offset.x(), offset.y(), offset.z());
        return new PasteSelection(selection.getRegion3d(), selection.getBound(), clipboard, offset, clipTrans, integrity);
    }

    @Override
    public void setForwardBlocks(EditWorld editWorld) {
        Clipboard newBackwardClip = new Clipboard();
        WorldEdits.copy(editWorld, this, Vector3d.ZERO, newBackwardClip);
        WorldEdits.paste(clipboard, editWorld, offset, clipTrans, integrity);
        backwardClip = newBackwardClip;
    }

    @Override
    public void setBackwardBlocks(EditWorld editWorld) {
        WorldEdits.paste(backwardClip, editWorld, Vector3d.ZERO);
    }

    @Override
    public PasteSelection affineTransform(AffineTransformation3d trans) {
        AffineTransformation3d newClipTrans = trans.linear().compose(this.clipTrans);
        Vector3d newOffset = trans.apply(offset);
        return newInstance(clipboard, newOffset, newClipTrans, integrity);
    }

}
