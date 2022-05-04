package tokyo.nakanaka.buildVoxCore.command.mixin.shapeMixin;

import picocli.CommandLine;
import tokyo.nakanaka.buildVoxCore.command.MissingPosDataException;
import tokyo.nakanaka.buildVoxCore.command.PosDataSizeException;
import tokyo.nakanaka.buildVoxCore.command.completionCandidates.PositiveIntegerCandidates;
import tokyo.nakanaka.buildVoxCore.math.vector.Vector3i;
import tokyo.nakanaka.buildVoxCore.selection.Selection;
import tokyo.nakanaka.buildVoxCore.selection.SelectionCreations;

public class FrameMixin implements ShapeMixin {
    @CommandLine.Option(names = {"-t", "--thickness"}, defaultValue = "1", completionCandidates = PositiveIntegerCandidates.class)
    private int thickness;

    public static final String DESCRIPTION = "a frame region of the cuboid by pos0 and pos1";

    @Override
    public Selection createSelection(Vector3i[] posArray) {
        if (posArray.length != 2) {
            throw new PosDataSizeException(2);
        }
        Vector3i pos0 = posArray[0];
        Vector3i pos1 = posArray[1];
        if (pos0 == null || pos1 == null) {
            throw new MissingPosDataException();
        }
        try {
            return SelectionCreations.createFrame(pos0, pos1, thickness);
        }catch (IllegalArgumentException ex) {
            throw new IllegalStateException();
        }
    }

}
