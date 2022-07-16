package tokyo.nakanaka.buildvox.core.command.mixin;

import picocli.CommandLine;
import tokyo.nakanaka.buildvox.core.block.VoxelBlock;

public class Replace {
    @CommandLine.Option(names = {"-r", "--replace"},
            description = "The block to replace",
            completionCandidates = Block.Candidates.class,
            converter = Block.Converter.class)
    private VoxelBlock filter;

    public VoxelBlock filter() {
        return filter;
    }

}
