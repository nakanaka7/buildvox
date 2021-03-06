package tokyo.nakanaka.buildvox.core.command.mixin;

import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;
import tokyo.nakanaka.buildvox.core.block.VoxelBlock;

import java.util.Iterator;

@Command
public class Block {
    private Block() {
    }

    @Parameters(description = "The Block.", completionCandidates = Candidates.class, converter = Converter.class)
    private VoxelBlock block;

    public VoxelBlock block() {
        return block;
    }

    public static class Candidates implements Iterable<String> {
        @Override
        public Iterator<String> iterator() {
            return BuildVoxSystem.getBlockRegistry().idList().stream()
                    .map(NamespacedId::toString)
                    .iterator();
        }
    }

    public static class Converter implements ITypeConverter<VoxelBlock> {
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

}
