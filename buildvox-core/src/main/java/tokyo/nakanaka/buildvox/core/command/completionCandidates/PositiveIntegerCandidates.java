package tokyo.nakanaka.buildvox.core.command.completionCandidates;

import java.util.Iterator;
import java.util.List;

public class PositiveIntegerCandidates implements Iterable<String> {

    @Override
    public Iterator<String> iterator() {
        return List.of("1", "2", "3", "4", "5", "6", "7", "8", "9").iterator();
    }

}
