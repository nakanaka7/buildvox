package tokyo.nakanaka.buildvox.core.selectionShape.shapeMixin;

import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.command.MissingPosException;
import tokyo.nakanaka.buildvox.core.command.PosArrayLengthException;
import tokyo.nakanaka.buildvox.core.command.completionCandidates.PositiveIntegerCandidates;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.selection.Selection;
import tokyo.nakanaka.buildvox.core.selection.SelectionCreations;

@Command
public class TriangleMixin implements ShapeMixin {
    @Option(names = {"-t", "--thickness"}, defaultValue = "1", completionCandidates = PositiveIntegerCandidates.class)
    private int thickness;

    public static final String DESCRIPTION = "a triangle region which vertexes are pos0, pos1, and pos2";

    @Override
    public Selection createSelection(Vector3i[] posArray) {
        if (posArray.length != 3) {
            throw new PosArrayLengthException(3);
        }
        Vector3i pos0 = posArray[0];
        Vector3i pos1 = posArray[1];
        Vector3i pos2 = posArray[2];
        if (pos0 == null || pos1 == null || pos2 == null) {
            throw new MissingPosException();
        }
        try{
            return SelectionCreations.createTriangle(pos0, pos1, pos2, thickness);
        }catch (IllegalArgumentException ex) {
            throw new IllegalStateException();
        }
    }

}
