package tokyo.nakanaka.buildVoxCore.command;

import picocli.CommandLine;
import tokyo.nakanaka.buildVoxCore.math.vector.Vector3d;
import tokyo.nakanaka.buildVoxCore.math.vector.Vector3i;

import java.util.Iterator;
import java.util.List;

public record Coordinate(Type type, double value) {
    public enum Type {
        ABSOLUTE,
        RELATIVE;
    }

    public static Vector3d calcAbsPos(Coordinate posX, Coordinate posY, Coordinate posZ, Vector3i execPos) {
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

    public static class Converter implements CommandLine.ITypeConverter<Coordinate> {
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
                return new Coordinate(Type.RELATIVE, relativeValue);
            } else {
                return new Coordinate(Type.ABSOLUTE, Double.parseDouble(value));
            }
        }
    }

    public static class CompletionCandidates implements Iterable<String> {
        @Override
        public Iterator<String> iterator() {
            return List.of("~").iterator();
        }
    }

}
