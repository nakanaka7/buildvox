package tokyo.nakanaka.buildvox.core;

import tokyo.nakanaka.buildvox.core.block.VoxelBlock;

public class BlockSettingProperties {
    private final double integrity;
    private final boolean masked;
    private final VoxelBlock[] filters;

    public BlockSettingProperties(Builder builder) {
        this.integrity = builder.integrity;
        this.masked = builder.masked;
        this.filters = builder.filters;
    }

    public static class Builder {
        private double integrity = 1;
        private boolean masked;
        private VoxelBlock[] filters;

        public Builder integrity(double integrity) {
            this.integrity = integrity;
            return this;
        }

        public Builder masked(boolean masked) {
            this.masked = masked;
            return this;
        }

        public Builder filters(VoxelBlock[] filters) {
            this.filters = filters;
            return this;
        }

        public BlockSettingProperties build() {
            return new BlockSettingProperties(this);
        }

    }

    public double getIntegrity() {
        return integrity;
    }

    public boolean getMasked() {
        return masked;
    }

    public VoxelBlock[] getFilters() {
        return filters;
    }

}
