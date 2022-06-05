package tokyo.nakanaka.buildvox.core;

import tokyo.nakanaka.buildvox.core.world.BlockState;

/* temporary */
public class BlockParserImpl implements BlockParser {
    @Override
    public BlockState parse(String s) {
        return BlockState.valueOf(s);
    }

}
