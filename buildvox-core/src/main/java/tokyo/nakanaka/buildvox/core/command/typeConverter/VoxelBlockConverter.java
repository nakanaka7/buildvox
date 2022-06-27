package tokyo.nakanaka.buildvox.core.command.typeConverter;

import picocli.CommandLine;
import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;
import tokyo.nakanaka.buildvox.core.world.VoxelBlock;

public class VoxelBlockConverter implements CommandLine.ITypeConverter<VoxelBlock> {
    @Override
    public VoxelBlock convert(String value) throws Exception {
        VoxelBlock block;
        try {
            block = VoxelBlock.valueOf(value);
        }catch (IllegalArgumentException ex) {
            throw new Exception();
        }
        boolean settable = BuildVoxSystem.getBlockValidator().validate(block);
        if(settable) {
            return block;
        }else {
            throw new Exception();
        }
    }

}
