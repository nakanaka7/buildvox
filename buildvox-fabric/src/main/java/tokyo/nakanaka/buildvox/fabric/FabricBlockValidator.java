package tokyo.nakanaka.buildvox.fabric;

import tokyo.nakanaka.buildvox.core.BlockValidator;
import tokyo.nakanaka.buildvox.core.world.BlockState;

/**
 * The implementation of BlockValidator for Fabric platform
 */
public class FabricBlockValidator implements BlockValidator {
    @Override
    public boolean validate(BlockState block) {
        if(block instanceof FabricBlockState)return true;
        String blockStr = block.toString();
        try{
            Utils.parseBlockState(blockStr);
        }catch (IllegalArgumentException e){
            return false;
        }
        return true;
    }
}
