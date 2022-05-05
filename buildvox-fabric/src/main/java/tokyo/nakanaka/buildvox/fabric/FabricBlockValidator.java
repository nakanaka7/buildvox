package tokyo.nakanaka.buildvox.fabric;

import tokyo.nakanaka.buildvox.core.BlockValidator;
import tokyo.nakanaka.buildvox.core.world.Block;

/**
 * The implementation of BlockValidator for Fabric platform
 */
public class FabricBlockValidator implements BlockValidator {
    @Override
    public boolean validate(Block block) {
        if(block instanceof FabricBlock)return true;
        String blockStr = block.toString();
        try{
            Utils.parseBlockState(blockStr);
        }catch (IllegalArgumentException e){
            return false;
        }
        return true;
    }
}
