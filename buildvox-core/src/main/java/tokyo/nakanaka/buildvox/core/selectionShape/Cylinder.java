package tokyo.nakanaka.buildvox.core.selectionShape;

import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.property.Axis;
import tokyo.nakanaka.buildvox.core.selection.Selection;
import tokyo.nakanaka.buildvox.core.selectionShape.util.SelectionCreations;

@Command
public class Cylinder implements SelectionShape {
    @Option(names = {"--axis"})
    private Axis axis = Axis.Y;

    public static final String DESCRIPTION = "a cylinder region in the cuboid by pos0 and pos1";

    @Override
    public Selection createSelection(Vector3i[] posArray) {
        if (posArray.length != 2) {
            throw new PosArrayLengthException(2);
        }
        Vector3i pos0 = posArray[0];
        Vector3i pos1 = posArray[1];
        return SelectionCreations.createCylinder(pos0, pos1, axis);
    }

}
