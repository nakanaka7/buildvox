package tokyo.nakanaka.buildvox.core;

import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.block.VoxelBlock;
import tokyo.nakanaka.buildvox.core.command.mixin.Block;
import tokyo.nakanaka.buildvox.core.command.mixin.Integrity;
import tokyo.nakanaka.buildvox.core.command.mixin.Masked;

/** Represents block setting options. */
public class BlockSettingOptions {
    @Mixin
    private Integrity integrity = new Integrity();

    @Mixin
    private Masked masked = new Masked();

    @Option(names = {"-r", "--replace"},
            description = "The blocks to replace",
            arity = "1..*",
            completionCandidates = Block.Candidates.class,
            converter = Block.Converter.class)
    private VoxelBlock[] replace;

    public void setIntegrity(double integrity) {
        this.integrity.setIntegrity(integrity);
    }

    public void setMasked(boolean masked) {
        this.masked.setMasked(masked);
    }

    public double getIntegrity() {
        return integrity.integrity();
    }

    public boolean getMasked() {
        return masked.masked();
    }

    public VoxelBlock[] getFilters() {
        return replace;
    }

}
