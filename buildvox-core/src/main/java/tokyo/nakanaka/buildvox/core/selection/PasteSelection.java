package tokyo.nakanaka.buildvox.core.selection;

import tokyo.nakanaka.buildvox.core.Clipboard;
import tokyo.nakanaka.buildvox.core.clientWorld.ClientWorld;
import tokyo.nakanaka.buildvox.core.edit.WorldEdits;
import tokyo.nakanaka.buildvox.core.math.region3d.Cuboid;
import tokyo.nakanaka.buildvox.core.math.region3d.Parallelepiped;
import tokyo.nakanaka.buildvox.core.math.region3d.Region3d;
import tokyo.nakanaka.buildvox.core.math.transformation.AffineTransformation3d;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;
import tokyo.nakanaka.buildvox.core.world.VoxelBlock;

public class PasteSelection extends BlockSelection {
    private final Clipboard clipboard;
    private final Vector3d pos;
    private final AffineTransformation3d clipTrans;

    private PasteSelection(Region3d region3d, Parallelepiped bound, Clipboard clipboard, Vector3d pos,
                           AffineTransformation3d clipTrans, double integrity) {
        this(region3d, bound, clipboard, pos, clipTrans, integrity, false, null);
    }

    private PasteSelection(Region3d region3d, Parallelepiped bound, Clipboard clipboard, Vector3d pos,
                           AffineTransformation3d clipTrans, double integrity,
                           boolean masked, VoxelBlock background) {
        super(region3d, bound);
        this.clipboard = clipboard;
        this.pos = pos;
        this.clipTrans = clipTrans;
        this.integrity = integrity;
        this.masked = masked;
        this.background = background;
    }

    public static PasteSelection newInstance(Clipboard clipboard, Vector3d pos, AffineTransformation3d clipTrans) {
        return newInstance(clipboard, pos, clipTrans, 1);
    }

    public static PasteSelection newInstance(Clipboard clipboard, Vector3d pos, AffineTransformation3d clipTrans, double integrity) {
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
                .translate(pos.x(), pos.y(), pos.z());
        return new PasteSelection(selection.getRegion3d(), selection.getBound(), clipboard, pos, clipTrans, integrity);
    }

    public static PasteSelection newInstance(Clipboard clipboard, Vector3d pos, AffineTransformation3d clipTrans,
            double integrity, boolean masked, VoxelBlock background) {
        double maxX = clipboard.maxX() + 1;
        double maxY = clipboard.maxY() + 1;
        double maxZ = clipboard.maxZ() + 1;
        double minX = clipboard.minX();
        double minY = clipboard.minY();
        double minZ = clipboard.minZ();
        Cuboid cuboid = new Cuboid(maxX, maxY, maxZ, minX, minY, minZ);
        var selection = new Selection(cuboid, cuboid)
                .affineTransform(clipTrans)
                .translate(pos.x(), pos.y(), pos.z());
        return new PasteSelection(selection.getRegion3d(), selection.getBound(), clipboard, pos, clipTrans, integrity,
                masked, background);
    }

    public static class Builder {
        private final Clipboard clipboard;
        private final Vector3d pos;
        private AffineTransformation3d clipTrans = AffineTransformation3d.IDENTITY;
        private double integrity;
        private boolean masked;

        public Builder(Clipboard clipboard, Vector3d pos) {
            this.clipboard = clipboard;
            this.pos = pos;
        }

        public Builder clipTrans(AffineTransformation3d clipTrans) {
            this.clipTrans = clipTrans;
            return this;
        }

        public Builder integrity(double integrity) {
            this.integrity = integrity;
            return this;
        }

        public Builder masked(boolean masked) {
            this.masked = masked;
            return this;
        }

        public PasteSelection build() {
            var i = newInstance(clipboard, pos, clipTrans);
            i.integrity = this.integrity;
            i.masked = this.masked;
            return i;
        }

    }

    void setRawForwardBlocks(ClientWorld clientWorld) {
        WorldEdits.paste(clipboard, clientWorld, pos, clipTrans);
    }

    @Override
    public PasteSelection affineTransform(AffineTransformation3d trans) {
        AffineTransformation3d newClipTrans = trans.linear().compose(this.clipTrans);
        Vector3d newOffset = trans.apply(pos);
        return newInstance(clipboard, newOffset, newClipTrans, integrity);
    }

}
