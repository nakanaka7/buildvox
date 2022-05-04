package tokyo.nakanaka.buildvox.core.command.mixin;

import picocli.CommandLine;
import tokyo.nakanaka.buildvox.core.command.Coordinate;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;

public class PosMixin {
    @CommandLine.Parameters(description = "x-coordinate of pos", converter = Coordinate.Converter.class, completionCandidates = Coordinate.CompletionCandidates.class)
    private Coordinate posX;
    @CommandLine.Parameters(description = "y-coordinate of pos", converter = Coordinate.Converter.class, completionCandidates = Coordinate.CompletionCandidates.class)
    private Coordinate posY;
    @CommandLine.Parameters(description = "z-coordinate of pos", converter = Coordinate.Converter.class, completionCandidates = Coordinate.CompletionCandidates.class)
    private Coordinate posZ;

    public Vector3d calcAbsPos(Vector3i execPos) {
        return Coordinate.calcAbsPos(posX, posY, posZ, execPos);
    }

}
