package tokyo.nakanaka.buildvox.fabric;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.BlockState;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.command.argument.BlockStateArgumentType;

public class Utils {
    private Utils() {
    }

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
