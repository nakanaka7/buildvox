package tokyo.nakanaka.buildVoxCore.command.completionCandidates;

import tokyo.nakanaka.buildVoxCore.NamespacedId;
import tokyo.nakanaka.buildVoxCore.system.BuildVoxSystem;

import java.util.Iterator;

public class BlockCandidates implements Iterable<String> {
    @Override
    public Iterator<String> iterator() {
        return BuildVoxSystem.BLOCK_REGISTRY.getBlockIdList().stream()
                .map(NamespacedId::toString)
                .iterator();
    }

}
