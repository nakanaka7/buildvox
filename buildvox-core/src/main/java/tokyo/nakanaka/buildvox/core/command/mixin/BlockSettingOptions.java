package tokyo.nakanaka.buildvox.core.command.mixin;

import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.BlockSettingArguments;
import tokyo.nakanaka.buildvox.core.block.VoxelBlock;

public class BlockSettingOptions {
    @Mixin
    private Integrity integrity;

    @Mixin
    private Masked masked;

    @Mixin
    private Replace replace;

    public void setIntegrity(double integrity) {
        this.integrity.setIntegrity(integrity);
    }

    public BlockSettingArguments getArguments() {
        var args = new BlockSettingArguments.Builder()
                .integrity(integrity.integrity())
                .masked(masked.masked());
        VoxelBlock filter = replace.filter();
        if(filter != null) {
            args = args.filters(filter);
        }
        return args.build();
    }

    public double getIntegrity() {
        return integrity.integrity();
    }

    public boolean getMasked() {
        return masked.masked();
    }

    public VoxelBlock getFilter() {
        return replace.filter();
    }

}
