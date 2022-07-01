package tokyo.nakanaka.buildvox.core.selectionShape.shapeMixin;

import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.command.MissingPosException;
import tokyo.nakanaka.buildvox.core.command.PosArrayLengthException;
import tokyo.nakanaka.buildvox.core.command.completionCandidates.PositiveIntegerCandidates;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.property.Axis;
import tokyo.nakanaka.buildvox.core.selection.Selection;
import tokyo.nakanaka.buildvox.core.selection.SelectionCreations;

@Command
public class PlateMixin implements ShapeMixin {
    @Option(names = {"-a", "--axis"})
    private Axis axis = Axis.Y;
    @Option(names = {"-t", "--thickness"}, defaultValue = "1", completionCandidates = PositiveIntegerCandidates.class)
    private int thickness;

    public static final String DESCRIPTION = "a plate region which corners are pos0 and pos1";

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
        try{
            return SelectionCreations.createPlate(pos0, pos1, axis, thickness);
        }catch (IllegalArgumentException ex) {
            throw new IllegalStateException();
        }
    }

}
