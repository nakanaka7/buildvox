package tokyo.nakanaka.buildvox.core.selectionShape;

import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.command.NumberCompletionCandidates;

public class Thickness {
    @Option(names = {"--thickness"},
            completionCandidates = NumberCompletionCandidates.PositiveInteger.class
            ,converter = Converter.class)
    private int thickness = 1;

    public int thickness() {
        return thickness;
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
