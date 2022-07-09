package tokyo.nakanaka.buildvox.core.selectionShape.util;

import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;

import java.util.Arrays;
import java.util.Iterator;

enum Direction {
    NORTH(Vector3i.MINUS_K),
    SOUTH(Vector3i.PLUS_K),
    EAST(Vector3i.PLUS_I),
    WEST(Vector3i.MINUS_I),
    UP(Vector3i.PLUS_J),
    DOWN(Vector3i.MINUS_J);
    private Vector3i v;

    Direction(Vector3i v) {
        this.v = v;
    }

    Vector3i toVector3i() {
        return v;
    }

    public static class CompletionCandidates implements Iterable<String> {
        @Override
        public Iterator<String> iterator() {
            return Arrays.stream(Direction.values())
                .map(s -> s.toString().toLowerCase())
                .iterator();
        }
    }

}
