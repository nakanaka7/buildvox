package tokyo.nakanaka.buildvox.core.brushSource;

import tokyo.nakanaka.buildvox.core.Axis;
import tokyo.nakanaka.buildvox.core.Clipboard;
import tokyo.nakanaka.buildvox.core.block.VoxelBlock;
import tokyo.nakanaka.buildvox.core.edit.VoxelSpaceEdits;
import tokyo.nakanaka.buildvox.core.math.region3d.Cylinder;
import tokyo.nakanaka.buildvox.core.math.region3d.Region3d;
import tokyo.nakanaka.buildvox.core.math.region3d.Sphere;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;
import tokyo.nakanaka.buildvox.core.selection.Selection;

/* experiment */
public class BrushSourceClipboards {
    private BrushSourceClipboards() {
    }

    public static Clipboard createSphere(VoxelBlock block, int size) {
        Region3d sphere = new Sphere(size * 0.5);
        Selection sel = new Selection(sphere, size * 0.5, size * 0.5, size * 0.5, -size * 0.5, -size * 0.5, -size * 0.5);
        if(size % 2 == 1) {
            sel = sel.translate(0.5, 0.5, 0.5);
        }
        Clipboard clipboard = new Clipboard();
        VoxelSpaceEdits.fill(clipboard, sel.calculateBlockPosSet(), block);
        return clipboard;
    }

    public static Clipboard createCylinder(VoxelBlock block, Axis axis, int diameter, int thickness) {
        if(diameter < 1) throw new IllegalArgumentException();
        if(thickness < 1) throw new IllegalArgumentException();
        double radius = (double) diameter / 2;
        Region3d cylinder = new Cylinder(radius, thickness);
        Selection sel = new Selection(cylinder, radius, radius, thickness, - radius, - radius, 0)
                .translate(0, 0, - (double) thickness / 2);
        switch (axis) {
            case X -> sel = sel.rotateY(Math.PI / 2);
            case Y -> sel = sel.rotateX(- Math.PI / 2);
        }
        if(diameter % 2 == 1) {
            Vector3d v = new Vector3d(1, 1, 1)
                    .subtract(axis.toVector3i().toVector3d())
                    .scalarMultiply(0.5);
            sel = sel.translate(v);
        }
        if(thickness % 2 == 1) {
            Vector3d v = axis.toVector3i().toVector3d().scalarMultiply(0.5);
            sel = sel.translate(v);
        }
        Clipboard clipboard = new Clipboard();
        VoxelSpaceEdits.fill(clipboard, sel.calculateBlockPosSet(), block);
        return clipboard;
    }

}
