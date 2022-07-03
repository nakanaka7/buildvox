package tokyo.nakanaka.buildvox.core.selectionShape.shape;

import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.edit.PlayerEdits;
import tokyo.nakanaka.buildvox.core.selectionShape.PosArrayLengthException;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.selection.Selection;
import tokyo.nakanaka.buildvox.core.selectionShape.SelectionCreations;

@Command
public class Line implements Shape {
    @Option(names = {"-t", "--thickness"}, defaultValue = "1")
    private double thickness;

    public static final String DESCRIPTION = "a line region which vertexes are pos0 and pos1";

    @Override
    public Selection createSelection(Vector3i[] posArray) {
        if (posArray.length != 2) {
            throw new PosArrayLengthException(2);
        }
        Vector3i pos0 = posArray[0];
        Vector3i pos1 = posArray[1];
        if (pos0 == null || pos1 == null) {
            throw new PlayerEdits.MissingPosException();
        }
        try {
            return SelectionCreations.createLine(pos0, pos1, thickness);
        }catch (IllegalArgumentException ex) {
            throw new IllegalStateException();
        }
    }

}
