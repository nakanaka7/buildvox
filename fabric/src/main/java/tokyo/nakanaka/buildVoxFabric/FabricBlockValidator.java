package tokyo.nakanaka.buildVoxFabric;

import tokyo.nakanaka.buildVoxCore.BlockValidator;
import tokyo.nakanaka.buildVoxCore.world.Block;

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
