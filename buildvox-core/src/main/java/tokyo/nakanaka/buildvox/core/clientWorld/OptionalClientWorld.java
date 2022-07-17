package tokyo.nakanaka.buildvox.core.clientWorld;

import tokyo.nakanaka.buildvox.core.block.VoxelBlock;
import tokyo.nakanaka.buildvox.core.BlockSettingOptions;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;

/** A client world which block-setting options applies to. */
public class OptionalClientWorld extends ClientWorld {
    private final ClientWorld delegate;

    /**
     * Creates a new instance from a player client world.
     * @param playerClientWorld a player client world.
     * @param options the block-setting options.
     */
    public OptionalClientWorld(PlayerClientWorld playerClientWorld, BlockSettingOptions options) {
        this(playerClientWorld, playerClientWorld.getPlayer().getBackgroundBlock(), options);
    }

    /**
     * Creates a new instance from a client world.
     * @param clientWorld the client world.
     * @param background the background block.
     * @param options the block-setting options.
     */
    public OptionalClientWorld(ClientWorld clientWorld, VoxelBlock background, BlockSettingOptions options) {
        super(clientWorld.getWorld(), clientWorld.getPhysics());
        ClientWorld dw = clientWorld;
        if(options.getMasked()) {
            dw = new MaskedClientWorld(background, dw);
        }
        dw = new IntegrityClientWorld(options.getIntegrity(), background, dw);
        if(options.getFilters()!= null) {
            dw = new ReplaceClientWorld(dw, options.getFilters());
        }
        this.delegate = dw;
    }

    private OptionalClientWorld(Builder builder) {
        super(builder.clientWorld.getWorld(), builder.clientWorld.getPhysics());
        ClientWorld dw = builder.clientWorld;
        if(builder.masked) {
            dw = new MaskedClientWorld(builder.background, dw);
        }
        if(builder.integrity != null) {
            dw = new IntegrityClientWorld(builder.integrity, builder.background, dw);
        }
        if(builder.filters != null) {
            dw = new ReplaceClientWorld(dw, builder.filters);
        }
        this.delegate = dw;
    }

    /* Builder */
    public static class Builder {
        private final ClientWorld clientWorld;
        private final VoxelBlock background;
        private Double integrity;
        private boolean masked;
        private VoxelBlock[] filters;

        /* Creates a new instance. */
        @Deprecated
        public Builder(ClientWorld clientWorld, VoxelBlock background) {
            this.clientWorld = clientWorld;
            this.background = background;
        }

        /* Set the integrity. */
        public Builder integrity(double integrity) {
            this.integrity = integrity;
            return this;
        }

        /* Set masked. */
        public Builder masked(boolean b) {
            this.masked = b;
            return this;
        }

        /* Set filters. */
        public Builder filters(VoxelBlock... filters) {
            this.filters = filters;
            return this;
        }

        /*
         * Returns a PlayerClientWorld instance.
         * @throws IllegalArgumentException if failed to build a new instance.
         */
        public OptionalClientWorld build() {
            try {
                return new OptionalClientWorld(this);
            }catch (Exception ex) {
                throw new IllegalArgumentException();
            }
        }

    }

    @Override
    public void setBlock(Vector3i pos, VoxelBlock block) {
        delegate.setBlock(pos, block);
    }

}
