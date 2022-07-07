package tokyo.nakanaka.buildvox.fabric.block;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.BlockState;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.command.argument.BlockStateArgumentType;
import tokyo.nakanaka.buildvox.core.block.VoxelBlock;

public class BlockUtils {
    private BlockUtils() {
    }

    /** Creates FabricBlockState */
    public static FabricBlockState createFabricBlockState(BlockState blockState) {
        return new FabricBlockState(blockState);
    }

    /**
     * Creates a BlockState
     * @throws IllegalArgumentException if fails to create.
     */
    public static BlockState createBlockState(VoxelBlock block) {
        String s = block.withoutEntity().toString();
        return parseBlockState(s);
    }

    /**
     * Parses a BlockState
     * @throws IllegalArgumentException if fails to create.
     */
    public static BlockState parseBlockState(String s) {
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
