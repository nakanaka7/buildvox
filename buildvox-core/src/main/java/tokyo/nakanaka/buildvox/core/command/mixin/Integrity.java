package tokyo.nakanaka.buildvox.core.command.mixin;

import picocli.CommandLine;

import java.util.Iterator;
import java.util.List;

public class Integrity {
    @CommandLine.Option(names = {"-i", "--integrity"}, description = "The integrity of block setting.",
            converter = Converter.class, completionCandidates = Candidates.class,
            defaultValue = "1",
            scope = CommandLine.ScopeType.INHERIT)
    private double integrity = 1;

    public double integrity() {
        return integrity;
    }

    public void setIntegrity(double integrity) {
        if(integrity < 0 || 1 < integrity) throw new IllegalArgumentException("integrity must be 0..1");
        this.integrity = integrity;
    }

    private static class Converter implements CommandLine.ITypeConverter<Double> {
        @Override
        public Double convert(String value) {
            var i = Double.parseDouble(value);
            if(i < 0 || 1 < i) throw new IllegalArgumentException("integrity must be 0..1");
            return i;
        }
    }

    private static class Candidates implements Iterable<String> {
        @Override
        public Iterator<String> iterator() {
            return List.of("0.1", "0.2", "0.3", "0.4", "0.5", "0.6",
                    "0.7", "0.8", "0.9", "1.0").iterator();
        }
    }

}
