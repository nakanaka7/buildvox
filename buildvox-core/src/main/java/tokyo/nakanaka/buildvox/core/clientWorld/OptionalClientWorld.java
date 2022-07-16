package tokyo.nakanaka.buildvox.core.clientWorld;

import tokyo.nakanaka.buildvox.core.block.VoxelBlock;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;

/* experimental */
public class OptionalClientWorld extends ClientWorld {
    private final ClientWorld delegate;

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

    /** Builder */
    public static class Builder {
        private final ClientWorld clientWorld;
        private final VoxelBlock background;
        private Double integrity;
        private boolean masked;
        private VoxelBlock[] filters;

        /** Creates a new instance. */
        public Builder(ClientWorld clientWorld, VoxelBlock background) {
            this.clientWorld = clientWorld;
            this.background = background;
        }

        /** Set the integrity. */
        public Builder integrity(double integrity) {
            this.integrity = integrity;
            return this;
        }

        /** Set masked. */
        public Builder masked(boolean b) {
            this.masked = b;
            return this;
        }

        /** Set filters. */
        public Builder filters(VoxelBlock... filters) {
            this.filters = filters;
            return this;
        }

        /**
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