package tokyo.nakanaka.buildvox.core.selectionShape;

import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.selection.Selection;
import tokyo.nakanaka.buildvox.core.selectionShape.util.SelectionCreations;
import tokyo.nakanaka.buildvox.core.selectionShape.mixin.Thickness;

@Command(description = "a triangle region which vertexes are pos0, pos1, and pos2")
public class Triangle implements SelectionShape {
    @Mixin
    private Thickness thickness;

    @Override
    public Selection createSelection(Vector3i[] posArray) {
        if (posArray.length != 3) {
            throw new PosArrayLengthException(3);
        }
        Vector3i pos0 = posArray[0];
        Vector3i pos1 = posArray[1];
        Vector3i pos2 = posArray[2];
        return SelectionCreations.createTriangle(pos0, pos1, pos2, thickness.thickness());
    }

}
