package tokyo.nakanaka.buildVoxCore.command.mixin.shapeMixin;

import picocli.CommandLine;
import tokyo.nakanaka.buildVoxCore.command.MissingPosDataException;
import tokyo.nakanaka.buildVoxCore.command.PosDataSizeException;
import tokyo.nakanaka.buildVoxCore.math.vector.Vector3i;
import tokyo.nakanaka.buildVoxCore.property.Axis;
import tokyo.nakanaka.buildVoxCore.selection.Selection;
import tokyo.nakanaka.buildVoxCore.selection.SelectionCreations;

public class CylinderMixin implements ShapeMixin {
    @CommandLine.Option(names = {"-a", "--axis"})
    private Axis axis = Axis.Y;

    public static final String DESCRIPTION = "a cylinder region in the cuboid by pos0 and pos1";

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
        return SelectionCreations.createCylinder(pos0, pos1, axis);
    }

}
