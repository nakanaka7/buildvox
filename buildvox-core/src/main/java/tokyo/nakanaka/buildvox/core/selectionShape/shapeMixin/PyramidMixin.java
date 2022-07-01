package tokyo.nakanaka.buildvox.core.selectionShape.shapeMixin;

import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.selectionShape.MissingPosException;
import tokyo.nakanaka.buildvox.core.selectionShape.PosArrayLengthException;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.property.Direction;
import tokyo.nakanaka.buildvox.core.selection.Selection;
import tokyo.nakanaka.buildvox.core.selection.SelectionCreations;

@Command
public class PyramidMixin implements ShapeMixin {
    @Option(names = {"-d", "--direction"}, completionCandidates = Direction.CompletionCandidates.class)
    private Direction direction = Direction.UP;

    public static final String DESCRIPTION = "a pyramid in the cuboid by pos0 and pos1";

    @Override
    public Selection createSelection(Vector3i[] posArray) {
        if (posArray.length != 2) {
            throw new PosArrayLengthException(2);
        }
        Vector3i pos0 = posArray[0];
        Vector3i pos1 = posArray[1];
        if (pos0 == null || pos1 == null) {
            throw new MissingPosException();
        }
        return SelectionCreations.createPyramid(pos0, pos1, direction);
    }

}
