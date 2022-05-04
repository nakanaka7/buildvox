package tokyo.nakanaka.buildVoxCore.blockSpace;

import tokyo.nakanaka.buildVoxCore.math.vector.Vector3i;

public class RecordingBlockSpace3<B> implements BlockSpace3<B> {
    private BlockSpace3<B> subjectSpace;
    private BlockSpace3<B> backupSpace;

    public RecordingBlockSpace3(BlockSpace3<B> subjectSpace, BlockSpace3<B> backupSpace) {
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
