package tokyo.nakanaka.buildvox.core;

import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;

public interface VoxelSpace<B> {
    B getBlock(Vector3i pos);
    void setBlock(Vector3i pos, B block);
}
