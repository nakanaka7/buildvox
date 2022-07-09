package tokyo.nakanaka.buildvox.core.selectionShape;

import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.selection.Selection;
import tokyo.nakanaka.buildvox.core.selectionShape.mixin.Axis;
import tokyo.nakanaka.buildvox.core.selectionShape.mixin.Thickness;
import tokyo.nakanaka.buildvox.core.selectionShape.util.SelectionCreations;

public class HollowPyramid implements SelectionShape {
    @Mixin
    private Axis axis;
    @Mixin
    private Thickness thickness;

    @Override
    public Selection createSelection(Vector3i[] posArray) {
        if (posArray.length != 2) {
            throw new PosArrayLengthException(2);
        }
        Vector3i pos0 = posArray[0];
        Vector3i pos1 = posArray[1];
        return SelectionCreations.createHollowPyramid(pos0, pos1, axis.axis(), thickness.thickness());
    }

}
