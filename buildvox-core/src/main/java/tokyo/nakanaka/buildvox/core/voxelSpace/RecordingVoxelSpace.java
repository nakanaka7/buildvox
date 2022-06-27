package tokyo.nakanaka.buildvox.core.voxelSpace;

import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;

public class RecordingVoxelSpace<B> implements VoxelSpace<B> {
    private VoxelSpace<B> subjectSpace;
    private VoxelSpace<B> backupSpace;

    public RecordingVoxelSpace(VoxelSpace<B> subjectSpace, VoxelSpace<B> backupSpace) {
        this.subjectSpace = subjectSpace;
        this.backupSpace = backupSpace;
    }

    @Override
    public B getBlock(Vector3i pos) {
        return subjectSpace.getBlock(pos);
    }

    /*
     * Pushes the block of the subject space to the backup space if a subject block exists.
     */
    @Override
    public void setBlock(Vector3i pos, B block) {
        B backupBlock = subjectSpace.getBlock(pos);
        if(backupBlock != null) {
            backupSpace.setBlock(pos, backupBlock);
        }
        subjectSpace.setBlock(pos, block);
    }

}
