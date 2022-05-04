package tokyo.nakanaka.buildVoxCore.blockSpace;

import tokyo.nakanaka.buildVoxCore.math.vector.Vector3i;

public interface BlockSpace3<B> {
    B getBlock(Vector3i pos);
    void setBlock(Vector3i pos, B block);
}
