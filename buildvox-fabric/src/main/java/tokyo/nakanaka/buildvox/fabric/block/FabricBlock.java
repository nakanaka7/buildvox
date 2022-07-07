package tokyo.nakanaka.buildvox.fabric.block;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.BlockState;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.command.argument.BlockStateArgumentType;
import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.block.Block;
import tokyo.nakanaka.buildvox.core.block.BlockTransformation;

/*
 * internal
 * Block implementation class for Fabric platform.
 */
public class FabricBlock implements Block<FabricBlockState, FabricBlockEntity> {
    private NamespacedId id;

    public FabricBlock(NamespacedId id) {
        this.id = id;
    }



    @Override
    public NamespacedId getId() {
        return id;
    }

    @Override
    public FabricBlockState transformState(FabricBlockState state, BlockTransformation blockTrans) {
        BlockState transState = state.transform(blockTrans);
        return new FabricBlockState(transState);
    }

    @Override
    public FabricBlockState parseState(String s) {
        String t = id.toString() + "[" + s + "]";
        var u = parseBlockState(t);
        return new FabricBlockState(u);
    }

    /**
     * Parses a BlockState
     * @throws IllegalArgumentException if fails to create.
     */
    private static BlockState parseBlockState(String s) {
        StringReader strReader = new StringReader(s);
        BlockStateArgument blockStateArg;
        try {
            blockStateArg = new BlockStateArgumentType().parse(strReader);
        } catch (CommandSyntaxException e) {
            throw new IllegalArgumentException("Cannot parse:" + s);
        }
        return blockStateArg.getBlockState();
    }

}
