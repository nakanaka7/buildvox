package tokyo.nakanaka.buildvox.fabric.block;

import tokyo.nakanaka.buildvox.core.block.BlockValidator;
import tokyo.nakanaka.buildvox.core.block.VoxelBlock;
import tokyo.nakanaka.buildvox.fabric.BlockUtils;

/*
 * Internal
 * The implementation of BlockValidator for Fabric platform
 */
public class FabricBlockValidator implements BlockValidator {
    @Override
    public boolean validate(VoxelBlock block) {
        try {
            BlockUtils.createBlockState(block);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
}
