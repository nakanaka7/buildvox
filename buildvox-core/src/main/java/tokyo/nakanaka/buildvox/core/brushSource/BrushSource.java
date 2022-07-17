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

    public Clipboard getClipboard() {
        return clipboard;
    }

    public BlockSettingOptions getOptions() {
        return options;
    }

}
