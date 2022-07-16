package tokyo.nakanaka.buildvox.core.command.mixin;

import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.BlockSettingProperties;
import tokyo.nakanaka.buildvox.core.block.VoxelBlock;

public class BlockSettingOptions {
    @Mixin
    private Integrity integrity;

    @Mixin
    private Masked masked;

    @Mixin
    private Replace replace;

    public BlockSettingProperties getBlockSettingProperties() {
        return new BlockSettingProperties.Builder()
                .integrity(integrity.integrity())
                .filters(replace.filter())
                .masked(masked.masked())
                .build();
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
