package tokyo.nakanaka.buildvox.core.selectionShape.shape;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import tokyo.nakanaka.buildvox.core.command.NumberCompletionCandidates;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.selection.Selection;
import tokyo.nakanaka.buildvox.core.selectionShape.PosArrayLengthException;
import tokyo.nakanaka.buildvox.core.selectionShape.SelectionCreations;

@Command
public class Triangle implements Shape {
    @Option(names = {"-t", "--thickness"}, defaultValue = "1", completionCandidates = NumberCompletionCandidates.PositiveInteger.class)
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
        try{
            return SelectionCreations.createTriangle(pos0, pos1, pos2, thickness);
        }catch (IllegalArgumentException ex) {
            throw new IllegalStateException();
        }
    }

}
