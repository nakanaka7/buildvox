package tokyo.nakanaka.buildvox.core.command.completionCandidates;

import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;

import java.util.Iterator;

public class BlockCandidates implements Iterable<String> {
    @Override
    public Iterator<String> iterator() {
        return BuildVoxSystem.getBlockRegistry().idList().stream()
                .map(NamespacedId::toString)
                .iterator();
    }

}
