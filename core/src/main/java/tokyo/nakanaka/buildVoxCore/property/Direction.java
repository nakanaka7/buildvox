package tokyo.nakanaka.buildVoxCore.property;

import java.util.Arrays;
import java.util.Iterator;

public enum Direction {
    NORTH, SOUTH, EAST, WEST, UP, DOWN;

    public static class CompletionCandidates implements Iterable<String> {
        @Override
        public Iterator<String> iterator() {
            return Arrays.stream(Direction.values())
                .map(s -> s.toString().toLowerCase())
                .iterator();
        }
    }

}
