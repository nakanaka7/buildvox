package tokyo.nakanaka.buildvox.core.brushSource;

import tokyo.nakanaka.buildvox.core.Clipboard;
import tokyo.nakanaka.buildvox.core.block.VoxelBlock;

/* experimental */
public class BrushSource {
    private final Clipboard clipboard;
    private double integrity;
    private VoxelBlock[] filters;
    private boolean masked;

    public BrushSource(Clipboard clipboard) {
        this.clipboard = clipboard;
    }

    private BrushSource(Builder builder) {
        this.clipboard = builder.clipboard;
        this.integrity = builder.integrity;
        this.filters = builder.filters;
        this.masked = builder.mask;
    }

    public static class Builder {
        private Clipboard clipboard;
        private double integrity = 1;
        private VoxelBlock[] filters;
        private boolean mask;
        public Builder(Clipboard clipboard) {
            this.clipboard = clipboard;
        }
        public Builder integrity(double integrity) {
            this.integrity = integrity;
            return this;
        }
        public Builder filters(VoxelBlock... filters) {
            this.filters = filters;
            return this;
        }
        public Builder masked(boolean mask) {
            this.mask = mask;
            return this;
        }
        public BrushSource build() {
            return new BrushSource(this);
        }
    }

    public Clipboard getClipboard() {
        return clipboard;
    }

    public double getIntegrity() {
        return integrity;
    }

    public VoxelBlock[] getFilters() {
        return filters;
    }

    public boolean getMasked() {
        return masked;
    }

}
