package tokyo.nakanaka.buildvox.core.selectionShape.mixin;

import picocli.CommandLine.Option;

import java.util.Iterator;
import java.util.List;

public class Hollow {
    @Option(names = "--hollow", completionCandidates = Candidates.class)
    private boolean hollow;

    private static class Candidates implements Iterable<String> {
        @Override
        public Iterator<String> iterator() {
            return List.of("true", "false").iterator();
        }
    }

    public boolean hollow() {
        return hollow;
    }

}
