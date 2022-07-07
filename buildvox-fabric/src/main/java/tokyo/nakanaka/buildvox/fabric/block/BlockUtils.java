package tokyo.nakanaka.buildvox.fabric.block;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.BlockState;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.state.property.Property;
import tokyo.nakanaka.buildvox.core.block.VoxelBlock;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BlockUtils {
    private BlockUtils() {
    }

    /** Creates FabricBlockState */
    public static FabricBlockState createFabricBlockState(BlockState blockState) {
        Collection<Property<?>> properties0 = blockState.getProperties();
        Map<String, String> stateMap = new HashMap<>();
        for(var key0 : properties0){
            Object value0 = blockState.get(key0);
            stateMap.put(key0.getName().toLowerCase(), value0.toString().toLowerCase());
        }
        return new FabricBlockState(stateMap);
    }

    /**
     * Creates a BlockState
     * @throws IllegalArgumentException if fails to create.
     */
    public static BlockState createBlockState(VoxelBlock block) {
        String s = block.withoutEntity().toString();
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
