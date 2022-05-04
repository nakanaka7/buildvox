package tokyo.nakanaka.buildVoxCore.blockSpace;

import tokyo.nakanaka.buildVoxCore.math.vector.Vector3i;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PlainBlockSpace3<B> implements BlockSpace3<B> {
    private Map<Vector3i, B> blockMap = new HashMap<>();

    @Override
    public B getBlock(Vector3i pos) {
        return blockMap.get(pos);
    }

    @Override
    public void setBlock(Vector3i pos, B block) {
        blockMap.put(pos, block);
    }

    public Set<Vector3i> blockPosSet() {
        return blockMap.keySet();
    }

}
