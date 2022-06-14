package tokyo.nakanaka.buildvox.core.system;

import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.block.Block;

import java.util.ArrayList;
import java.util.List;

public class BlockRegistry {
    public static List<NamespacedId> blockIdList = new ArrayList<>();
    /**
     * Add a block id which is shown on tab complete.
     * @param blockId a block id.
     */
    @SuppressWarnings("unused")
    public void register(NamespacedId blockId) {
        if(!blockIdList.contains(blockId)) {
            blockIdList.add(blockId);
        }
    }

    public void register(Block<?, ?> block) {
        register(block.getId());
    }

    public List<NamespacedId> idList() {
        return blockIdList;
    }

}
