package tokyo.nakanaka.buildVoxCore.system;

import tokyo.nakanaka.buildVoxCore.NamespacedId;

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

    public List<NamespacedId> getBlockIdList() {
        return blockIdList;
    }

}
