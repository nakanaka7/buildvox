package tokyo.nakanaka.buildvox.core.selectionShape.shapeMixin;

import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.edit.PlayerEdits;
import tokyo.nakanaka.buildvox.core.selectionShape.PosArrayLengthException;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.selection.Selection;
import tokyo.nakanaka.buildvox.core.selectionShape.SelectionCreations;

@Command
public class EllipseMixin implements ShapeMixin {

    public static final String DESCRIPTION = "a ellipse region in the cuboid by pos0 and pos1";

    @Override
    public Selection createSelection(Vector3i[] posArray) {
        if (posArray.length != 2) {
            throw new PosArrayLengthException(2);
        }
        Vector3i pos0 = posArray[0];
        Vector3i pos1 = posArray[1];
        if (pos0 == null || pos1 == null) {
            throw new PlayerEdits.MissingPosException();
        }
        return SelectionCreations.createEllipse(pos0, pos1);
    }

}
