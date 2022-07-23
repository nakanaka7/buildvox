package tokyo.nakanaka.buildvox.core.command.mixin;

import picocli.CommandLine;

import java.util.Iterator;
import java.util.List;

public class PositiveInteger {
    public static class PositiveIntegerCandidates implements Iterable<String> {
        @Override
        public Iterator<String> iterator() {
            return List.of("1", "2", "3", "4", "5", "6", "7", "8", "9").iterator();
        }
    }

    public static class PositiveIntegerConverter implements CommandLine.ITypeConverter<Integer> {
        @Override
        public Integer convert(String value) {
            int i = Integer.parseInt(value);
            if(i < 1) throw new IllegalArgumentException();
            return i;
        }
    }
}
