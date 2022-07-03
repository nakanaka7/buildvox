package tokyo.nakanaka.buildvox.core.selectionShape.mixin;

import picocli.CommandLine.*;

import java.util.Iterator;
import java.util.List;

public class Thickness {
    @Option(names = {"--thickness"},
            completionCandidates = Candidates.class
            ,converter = Converter.class)
    private int thickness = 1;

    public int thickness() {
        return thickness;
    }

    private static class Candidates implements Iterable<String> {
        @Override
        public Iterator<String> iterator() {
            return List.of("1", "2", "3").iterator();
        }
    }

    private static class Converter implements ITypeConverter<Integer> {
        @Override
        public Integer convert(String value) {
            int i = Integer.parseInt(value);
            if(i <= 0) throw new IllegalArgumentException();
            return i;
        }
    }

}
