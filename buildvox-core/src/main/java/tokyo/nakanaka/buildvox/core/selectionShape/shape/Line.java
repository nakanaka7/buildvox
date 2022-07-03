package tokyo.nakanaka.buildvox.core.selectionShape.shape;

import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.selection.Selection;
import tokyo.nakanaka.buildvox.core.selectionShape.PosArrayLengthException;
import tokyo.nakanaka.buildvox.core.selectionShape.SelectionCreations;
import tokyo.nakanaka.buildvox.core.selectionShape.SelectionShape;
import tokyo.nakanaka.buildvox.core.selectionShape.shape.mixin.Thickness;

@Command
public class Line implements SelectionShape {
    @Mixin
    private Thickness thickness;

    public static final String DESCRIPTION = "a line region which vertexes are pos0 and pos1";

    @Override
    public Selection createSelection(Vector3i[] posArray) {
        if (posArray.length != 2) {
            throw new PosArrayLengthException(2);
        }
        Vector3i pos0 = posArray[0];
        Vector3i pos1 = posArray[1];
        return SelectionCreations.createLine(pos0, pos1, thickness.thickness());
    }

}
