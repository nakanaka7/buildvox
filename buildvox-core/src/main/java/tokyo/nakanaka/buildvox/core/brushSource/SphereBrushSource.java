package tokyo.nakanaka.buildvox.core.brushSource;

import tokyo.nakanaka.buildvox.core.Clipboard;
import tokyo.nakanaka.buildvox.core.block.VoxelBlock;
import tokyo.nakanaka.buildvox.core.edit.VoxelSpaceEdits;
import tokyo.nakanaka.buildvox.core.math.region3d.Region3d;
import tokyo.nakanaka.buildvox.core.math.region3d.Sphere;
import tokyo.nakanaka.buildvox.core.selection.Selection;

/* experimental */
public class SphereBrushSource extends BrushSource {
    private SphereBrushSource(Clipboard clipboard) {
        super(clipboard);
    }

    public static SphereBrushSource newInstance(VoxelBlock block, int size) {
        Region3d sphere = new Sphere(size * 0.5);
        Selection sel = new Selection(sphere, size * 0.5, size * 0.5, size * 0.5, -size * 0.5, -size * 0.5, -size * 0.5);
        if(size % 2 == 1) {
            sel = sel.translate(0.5, 0.5, 0.5);
        }
        Clipboard clipboard = new Clipboard();
        VoxelSpaceEdits.fill(clipboard, sel.calculateBlockPosSet(), block);
        return new SphereBrushSource(clipboard);
    }

}
