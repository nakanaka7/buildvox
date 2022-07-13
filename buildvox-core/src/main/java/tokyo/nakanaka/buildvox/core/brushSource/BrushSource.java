package tokyo.nakanaka.buildvox.core.brushSource;

import tokyo.nakanaka.buildvox.core.Clipboard;

/* experimental */
public class BrushSource {
    private final Clipboard clipboard;

    public BrushSource(Clipboard clipboard) {
        this.clipboard = clipboard;
    }

    public Clipboard getClipboard() {
        return clipboard;
    }

}
