package tokyo.nakanaka.buildvox.core.selectionShape.shape;

import tokyo.nakanaka.buildvox.core.command.NumberCompletionCandidates;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.selection.Selection;
import tokyo.nakanaka.buildvox.core.selectionShape.PosArrayLengthException;
import tokyo.nakanaka.buildvox.core.selectionShape.SelectionCreations;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Option;

@Command
public class Frame implements Shape {
    @Option(names = {"-t", "--thickness"}, defaultValue = "1", completionCandidates = NumberCompletionCandidates.PositiveInteger.class)
    private int thickness;

    public static final String DESCRIPTION = "a frame region of the cuboid by pos0 and pos1";

    @Override
    public Selection createSelection(Vector3i[] posArray) {
        if (posArray.length != 2) {
            throw new PosArrayLengthException(2);
        }
        Vector3i pos0 = posArray[0];
        Vector3i pos1 = posArray[1];
        try {
            return SelectionCreations.createFrame(pos0, pos1, thickness);
        }catch (IllegalArgumentException ex) {
            throw new IllegalStateException();
        }
    }

}
