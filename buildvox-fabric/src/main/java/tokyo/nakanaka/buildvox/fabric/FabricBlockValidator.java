package tokyo.nakanaka.buildvox.fabric;

import tokyo.nakanaka.buildvox.core.block.BlockValidator;
import tokyo.nakanaka.buildvox.core.world.VoxelBlock;

/**
 * The implementation of BlockValidator for Fabric platform
 */
public class FabricBlockValidator implements BlockValidator {
    @Override
    public boolean validate(VoxelBlock block) {
        if(block instanceof FabricVoxelBlock)return true;
        String blockStr = block.withoutEntity().toString();
        try{
            BlockUtils.parseBlockState(blockStr);
        }catch (IllegalArgumentException e){
            return false;
        }
        return true;
    }
}
