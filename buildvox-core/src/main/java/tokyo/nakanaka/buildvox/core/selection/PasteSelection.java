package tokyo.nakanaka.buildvox.core.selection;

import tokyo.nakanaka.buildvox.core.Clipboard;
import tokyo.nakanaka.buildvox.core.clientWorld.ClientWorld;
import tokyo.nakanaka.buildvox.core.edit.WorldEdits;
import tokyo.nakanaka.buildvox.core.math.region3d.Cuboid;
import tokyo.nakanaka.buildvox.core.math.transformation.AffineTransformation3d;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;

/**
 * Represents the selection when pasting.
 */
public class PasteSelection extends BlockSelection {
    private final Clipboard clipboard;
    private final Vector3d pos;
    private final AffineTransformation3d clipTrans;

    private PasteSelection(Selection selection, Clipboard clipboard, Vector3d pos,
                           AffineTransformation3d clipTrans) {
        super(selection.getRegion3d(), selection.getBound());
        this.clipboard = clipboard;
        this.pos = pos;
        this.clipTrans = clipTrans;
    }

    private static Cuboid calcClipboardCuboid(Clipboard clipboard) {
        double maxX = clipboard.maxX() + 1;
        double maxY = clipboard.maxY() + 1;
        double maxZ = clipboard.maxZ() + 1;
        double minX = clipboard.minX();
        double minY = clipboard.minY();
        double minZ = clipboard.minZ();
        return new Cuboid(maxX, maxY, maxZ, minX, minY, minZ);
    }

    /**
     * The builder class of PasteSelection.
     */
    public static class Builder {
        private final Selection selection;
        private final Clipboard clipboard;
        private final Vector3d pos;
        private AffineTransformation3d clipTrans = AffineTransformation3d.IDENTITY;
        private double integrity = 1;
        private boolean masked;

        /**
         * Creates a new instance.
         * @param clipboard the clipboard.
         * @param pos the position to paste.
         * @param selection the base selection.
         */
        public Builder(Clipboard clipboard, Vector3d pos, Selection selection) {
            this.clipboard = clipboard;
            this.pos = pos;
            this.selection = selection;
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
            var i = new PasteSelection(selection, clipboard, pos, clipTrans);
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
        Selection newSel = toNonBlock().affineTransform(trans);
        return new Builder(clipboard, transPos, newSel)
                .clipTrans(newClipTrans)
                .integrity(this.integrity)
                .masked(this.masked)
                .build();
    }

}
