package tokyo.nakanaka.buildvox.core.selectionShape;

import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.selection.Selection;
import tokyo.nakanaka.buildvox.core.selectionShape.util.SelectionCreations;
import tokyo.nakanaka.buildvox.core.selectionShape.mixin.Thickness;

import static picocli.CommandLine.Command;

@Command(description = "a frame region of the cuboid by pos0 and pos1")
public class Frame implements SelectionShape {
    @Mixin
    private Thickness thickness;

    @Override
    public Selection createSelection(Vector3i[] posArray) {
        if (posArray.length != 2) {
            throw new PosArrayLengthException(2);
        }
        Vector3i pos0 = posArray[0];
        Vector3i pos1 = posArray[1];
        return SelectionCreations.createFrame(pos0, pos1, thickness.thickness());
    }

}
