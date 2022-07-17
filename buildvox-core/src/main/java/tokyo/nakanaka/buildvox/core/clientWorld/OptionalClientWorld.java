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
        if(options.getReplaces()!= null) {
            dw = new ReplaceClientWorld(dw, options.getReplaces());
        }
        this.delegate = dw;
    }

    @Override
    public void setBlock(Vector3i pos, VoxelBlock block) {
        delegate.setBlock(pos, block);
    }

}
