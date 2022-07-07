package tokyo.nakanaka.buildvox.fabric.block;

import tokyo.nakanaka.buildvox.core.block.BlockValidator;
import tokyo.nakanaka.buildvox.core.block.VoxelBlock;

/*
 * Internal
 * The implementation of BlockValidator for Fabric platform
 */
public class FabricBlockValidator implements BlockValidator {
    @Override
    public boolean validate(VoxelBlock block) {
        var state = block.getState();
        var entity = block.getEntity();
        if(entity == null) {
            return state instanceof FabricBlockState;
        }else {
            return state instanceof FabricBlockState && entity instanceof FabricBlockEntity;
        }
    }
}
