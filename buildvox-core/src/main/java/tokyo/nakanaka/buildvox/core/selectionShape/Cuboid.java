package tokyo.nakanaka.buildvox.core.selectionShape;

import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.selection.Selection;
import tokyo.nakanaka.buildvox.core.selectionShape.util.SelectionCreations;

import static picocli.CommandLine.Command;

@Command(description = "a cuboid by pos0 and pos1")
public class Cuboid implements SelectionShape {

    @Override
    public Selection createSelection(Vector3i[] posArray) {
        if(posArray.length != 2)throw new PosArrayLengthException(2);
        Vector3i pos0 = posArray[0];
        Vector3i pos1 = posArray[1];
        return SelectionCreations.createCuboid(pos0, pos1);
    }

}
