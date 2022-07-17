package tokyo.nakanaka.buildvox.core.command.mixin;

import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.block.VoxelBlock;

public class BlockSettingOptions {
    @Mixin
    private Integrity integrity = new Integrity();

    @Mixin
    private Masked masked = new Masked();

    @Mixin
    private Replace replace = new Replace();

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
        var filter = replace.filter();
        if(filter == null) {
            return null;
        }else{
            return new VoxelBlock[]{filter};
        }
    }

}
