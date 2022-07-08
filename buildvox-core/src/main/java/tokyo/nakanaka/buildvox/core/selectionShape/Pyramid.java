package tokyo.nakanaka.buildvox.core.selectionShape;

import picocli.CommandLine;
import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.Axis;
import tokyo.nakanaka.buildvox.core.selection.Selection;
import tokyo.nakanaka.buildvox.core.selectionShape.mixin.Hollow;
import tokyo.nakanaka.buildvox.core.selectionShape.util.SelectionCreations;

@Command(description = "a pyramid in the cuboid by pos0 and pos1")
public class Pyramid implements SelectionShape {
    @Option(names = {"--axis"})
    private Axis axis = Axis.Y;
    @CommandLine.Mixin
    private Hollow hollow;

    @Override
    public Selection createSelection(Vector3i[] posArray) {
        if (posArray.length != 2) {
            throw new PosArrayLengthException(2);
        }
        Vector3i pos0 = posArray[0];
        Vector3i pos1 = posArray[1];
        if(!hollow.hollow()) {
            return SelectionCreations.createPyramid(pos0, pos1, axis);
        }else{
            return SelectionCreations.createHollowPyramid(pos0, pos1, axis, 1);
        }
    }

}
