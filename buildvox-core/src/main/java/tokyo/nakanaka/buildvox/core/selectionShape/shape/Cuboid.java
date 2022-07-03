package tokyo.nakanaka.buildvox.core.selectionShape.shape;

import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.selection.Selection;
import tokyo.nakanaka.buildvox.core.selectionShape.PosArrayLengthException;
import tokyo.nakanaka.buildvox.core.selectionShape.util.SelectionCreations;
import tokyo.nakanaka.buildvox.core.selectionShape.SelectionShape;

import static picocli.CommandLine.Command;

@Command
public class Cuboid implements SelectionShape {

    public static final String DESCRIPTION = "a cuboid by pos0 and pos1";

    @Override
    public Selection createSelection(Vector3i[] posArray) {
        if(posArray.length != 2)throw new PosArrayLengthException(2);
        Vector3i pos0 = posArray[0];
        Vector3i pos1 = posArray[1];
        return SelectionCreations.createCuboid(pos0, pos1);
    }

}
