package tokyo.nakanaka.buildvox.core;

import tokyo.nakanaka.buildvox.core.block.BlockParser;
import tokyo.nakanaka.buildvox.core.world.VoxelBlock;

/* temporary */
public class BlockParserImpl implements BlockParser {
    @Override
    public VoxelBlock parse(String s) {
        return VoxelBlock.valueOf(s);
    }

}
