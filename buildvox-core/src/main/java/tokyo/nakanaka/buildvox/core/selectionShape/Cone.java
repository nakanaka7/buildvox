package tokyo.nakanaka.buildvox.core.selectionShape;

import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.property.Direction;
import tokyo.nakanaka.buildvox.core.selection.Selection;
import tokyo.nakanaka.buildvox.core.selectionShape.util.SelectionCreations;

@Command
public class Cone implements SelectionShape {
    @Option(names = {"--direction"}, completionCandidates = Direction.CompletionCandidates.class)
    private Direction direction = Direction.UP;

    public static final String DESCRIPTION = "a cone region in the cuboid by pos0 and pos1";

    @Override
    public Selection createSelection(Vector3i[] posArray) {
        if (posArray.length != 2) {
            throw new PosArrayLengthException(2);
        }
        Vector3i pos0 = posArray[0];
        Vector3i pos1 = posArray[1];
        return SelectionCreations.createCone(pos0, pos1, direction);
    }

}
