package tokyo.nakanaka.buildvox.core.selectionShape;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.selection.Selection;
import tokyo.nakanaka.buildvox.core.selectionShape.mixin.Hollow;
import tokyo.nakanaka.buildvox.core.selectionShape.util.SelectionCreations;

@Command(description = "a torus region in the cuboid by pos0 and pos1")
public class Torus implements SelectionShape {
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
            return SelectionCreations.createTorus(pos0, pos1);
        }else{
            return SelectionCreations.createHollowTorus(pos0, pos1, 1);
        }
    }

}
