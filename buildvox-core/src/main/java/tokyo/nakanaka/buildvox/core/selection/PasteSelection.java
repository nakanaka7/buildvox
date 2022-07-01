package tokyo.nakanaka.buildvox.core.selection;

import tokyo.nakanaka.buildvox.core.Clipboard;
import tokyo.nakanaka.buildvox.core.clientWorld.ClientWorld;
import tokyo.nakanaka.buildvox.core.edit.WorldEdits;
import tokyo.nakanaka.buildvox.core.math.region3d.Cuboid;
import tokyo.nakanaka.buildvox.core.math.region3d.Parallelepiped;
import tokyo.nakanaka.buildvox.core.math.region3d.Region3d;
import tokyo.nakanaka.buildvox.core.math.transformation.AffineTransformation3d;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;

/**
 * Represents the selection when pasting.
 */
public class PasteSelection extends BlockSelection {
    private final Clipboard clipboard;
    private final Vector3d pos;
    private final AffineTransformation3d clipTrans;

    private PasteSelection(Region3d region3d, Parallelepiped bound, Clipboard clipboard, Vector3d pos,
                           AffineTransformation3d clipTrans) {
        super(region3d, bound);
        this.clipboard = clipboard;
        this.pos = pos;
        this.clipTrans = clipTrans;
    }

    private static PasteSelection newInstance(Clipboard clipboard, Vector3d pos, AffineTransformation3d clipTrans) {
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
        return new PasteSelection(selection.getRegion3d(), selection.getBound(), clipboard, pos, clipTrans);
    }

    /**
     * The builder class of PasteSelection.
     */
    public static class Builder {
        private final Clipboard clipboard;
        private final Vector3d pos;
        private AffineTransformation3d clipTrans = AffineTransformation3d.IDENTITY;
        private double integrity = 1;
        private boolean masked;

        /**
         * Creates a new instance.
         * @param clipboard the clipboard.
         * @param pos the position to paste.
         */
        public Builder(Clipboard clipboard, Vector3d pos) {
            this.clipboard = clipboard;
            this.pos = pos;
        }

        /**
         * Sets the clipboard transformation.
         */
        public Builder clipTrans(AffineTransformation3d clipTrans) {
            this.clipTrans = clipTrans;
            return this;
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
        Vector3d transPos = trans.apply(pos);
        return new Builder(clipboard, transPos)
                .clipTrans(newClipTrans)
                .integrity(this.integrity)
                .masked(this.masked)
                .build();
    }

}
