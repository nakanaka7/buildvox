package tokyo.nakanaka.buildvox.core.voxelSpace;

import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;

//integrity: https://www.computerhope.com/jargon/i/integrit.htm
public class IntegrityAdjustableVoxelSpace<B> implements VoxelSpace<B> {
    private VoxelSpace<B> subjectSpace;
    private double integrity;

    /**
     * @throws IllegalArgumentException if integrity is less than 0 or larger than 1
     */
    public IntegrityAdjustableVoxelSpace(VoxelSpace<B> subjectSpace, double integrity) {
        this.subjectSpace = subjectSpace;
        if(integrity < 0 || 1 < integrity)throw new IllegalArgumentException();
        this.integrity = integrity;
    }

    @Override
    public B getBlock(Vector3i pos) {
        return subjectSpace.getBlock(pos);
    }

    @Override
    public void setBlock(Vector3i pos, B block) {
        double random = Math.random();
        if(random < integrity) {
            subjectSpace.setBlock(pos, block);
        }
    }

}
