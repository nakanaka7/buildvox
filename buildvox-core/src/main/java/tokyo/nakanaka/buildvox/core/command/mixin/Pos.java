package tokyo.nakanaka.buildvox.core.command.mixin;

import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;

import java.util.Iterator;
import java.util.List;

public class Pos {
    @Parameters(description = "x-coordinate of pos. default:~", defaultValue = "~", converter = Coordinate.Converter.class, completionCandidates = Coordinate.CompletionCandidates.class)
    private Coordinate posX;
    @Parameters(description = "y-coordinate of pos. default:~", defaultValue = "~", converter = Coordinate.Converter.class, completionCandidates = Coordinate.CompletionCandidates.class)
    private Coordinate posY;
    @Parameters(description = "z-coordinate of pos. default:~", defaultValue = "~", converter = Coordinate.Converter.class, completionCandidates = Coordinate.CompletionCandidates.class)
    private Coordinate posZ;

    private static record Coordinate(Type type, double value) {
        private enum Type {
            ABSOLUTE,
            RELATIVE;
        }

        public static class Converter implements ITypeConverter<Coordinate> {
            @Override
            public Coordinate convert(String value) {
                if (value.startsWith("~")) {
                    String s = value.substring(1);
                    double relativeValue;
                    if (s.isEmpty()) {
                        relativeValue = 0;
                    } else {
                        relativeValue = Double.parseDouble(s);
                    }
                    return new Coordinate(Coordinate.Type.RELATIVE, relativeValue);
                } else {
                    return new Coordinate(Coordinate.Type.ABSOLUTE, Double.parseDouble(value));
                }
            }
        }

        private static class CompletionCandidates implements Iterable<String> {
            @Override
            public Iterator<String> iterator() {
                return List.of("~").iterator();
            }
        }

    }

    /**
     * @param execPos the execution position of the command.
     * @return the position.
     */
    public Vector3d toVector3d(Vector3i execPos) {
        double absX = calcAbsCoordinate(posX, execPos.x());
        double absY = calcAbsCoordinate(posY, execPos.y());
        double absZ = calcAbsCoordinate(posZ, execPos.z());
        return new Vector3d(absX, absY, absZ);
    }

    private static double calcAbsCoordinate(Coordinate coordinate, double execCoordinate) {
        return switch (coordinate.type()) {
            case ABSOLUTE -> coordinate.value();
            case RELATIVE -> coordinate.value() + execCoordinate;
        };
    }

}
