package tokyo.nakanaka.buildvox.core.brushSource;

import tokyo.nakanaka.buildvox.core.BlockSettingOptions;
import tokyo.nakanaka.buildvox.core.Clipboard;

/* experimental */
public class BrushSource {
    private final Clipboard clipboard;
    private final BlockSettingOptions options;

    public BrushSource(Clipboard clipboard, BlockSettingOptions options) {
        this.clipboard = clipboard;
        this.options = options;
    }

    public BrushSource(Clipboard clipboard) {
        this(clipboard, new BlockSettingOptions());
    }

    private BrushSource(Builder builder) {
        this.clipboard = builder.clipboard;
        this.options = new BlockSettingOptions();
        this.options.setIntegrity(builder.integrity);
        this.options.setMasked(builder.mask);
    }

    public static class Builder {
        private Clipboard clipboard;
        private double integrity = 1;
        private boolean mask;
        @Deprecated
        public Builder(Clipboard clipboard) {
            this.clipboard = clipboard;
        }
        public Builder integrity(double integrity) {
            this.integrity = integrity;
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

    public BlockSettingOptions getOptions() {
        return options;
    }

}
