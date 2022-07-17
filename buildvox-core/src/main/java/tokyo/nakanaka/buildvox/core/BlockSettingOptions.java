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
    private VoxelBlock[] replace; //not "replaces", because picocli shows "-r = <replace>..."

    /** Sets the integrity. */
    public void setIntegrity(double integrity) {
        this.integrity.setIntegrity(integrity);
    }

    /** Sets the masked. */
    public void setMasked(boolean masked) {
        this.masked.setMasked(masked);
    }

    /** Sets the replaces */
    public void setReplaces(VoxelBlock... replaces) {
        this.replace = replaces;
    }

    /** Gets the integrity. */
    public double getIntegrity() {
        return integrity.integrity();
    }

    /** Gets the masked. */
    public boolean getMasked() {
        return masked.masked();
    }

    /** Gets the replaces. null return means all blocks must be replaced. */
    public VoxelBlock[] getReplaces() {
        return replace;
    }

}
