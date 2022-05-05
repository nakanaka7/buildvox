package tokyo.nakanaka.buildvox.core.blockSpace;

import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;

public interface BlockSpace3<B> {
    B getBlock(Vector3i pos);
    void setBlock(Vector3i pos, B block);
}
