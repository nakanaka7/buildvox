package tokyo.nakanaka.buildvox.fabric.block;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.BlockState;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.block.Block;
import tokyo.nakanaka.buildvox.core.block.BlockTransformation;
import tokyo.nakanaka.buildvox.core.math.transformation.Matrix3x3i;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;

/*
 * internal
 * Block implementation class for Fabric platform.
 */
public class FabricBlock implements Block<FabricBlockState, FabricBlockEntity> {
    protected final NamespacedId id;

    public FabricBlock(NamespacedId id) {
        this.id = id;
    }

    @Override
    public NamespacedId getId() {
        return id;
    }

    @Override
    public FabricBlockState transformState(FabricBlockState state, BlockTransformation blockTrans) {
        BlockState transState = transform(state.getBlockState(), blockTrans);
        return new FabricBlockState(transState);
    }

    private static BlockState transform(BlockState blockState, BlockTransformation blockTrans) {
        Matrix3x3i transMatrix = blockTrans.toMatrix3x3i();
        Vector3i transI = transMatrix.apply(Vector3i.PLUS_I);
        Vector3i transJ = transMatrix.apply(Vector3i.PLUS_J);
        Vector3i transK = transMatrix.apply(Vector3i.PLUS_K);
        BlockState transState;
        if(transJ.equals(Vector3i.PLUS_J) || transJ.equals(Vector3i.MINUS_J)) {
            if (transK.equals(Vector3i.PLUS_K)) {
                if (transI.equals(Vector3i.PLUS_I)) {
                    transState = blockState;
                } else {//transI.equals(Vector3d.MINUS_I)
                    transState = blockState.mirror(BlockMirror.FRONT_BACK);
                }
            } else if (transK.equals(Vector3i.PLUS_I)) {
                if (transI.equals(Vector3i.MINUS_K)) {
                    transState = blockState.rotate(BlockRotation.COUNTERCLOCKWISE_90);
                } else {//transI.equals(Vector3d.PLUS_K)
                    transState = blockState.mirror(BlockMirror.FRONT_BACK)
                            .rotate(BlockRotation.COUNTERCLOCKWISE_90);
                }
            } else if (transK.equals(Vector3i.MINUS_K)) {
                if (transI.equals(Vector3i.PLUS_I)) {
                    transState = blockState.mirror(BlockMirror.LEFT_RIGHT);
                } else{//transI.equals(Vector3d.MINUS_I)
                    transState = blockState.rotate(BlockRotation.CLOCKWISE_180);
                }
            } else{//transK.equals(Vector3d.MINUS_I)
                if (transI.equals(Vector3i.PLUS_K)) {
                    transState = blockState.rotate(BlockRotation.CLOCKWISE_90);
                } else {//transI.equals(Vector3d.MINUS_K))
                    transState = blockState.rotate(BlockRotation.CLOCKWISE_90)
                            .mirror(BlockMirror.LEFT_RIGHT);
                }
            }
        }else {
            transState = blockState;
        }
        return transState;
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
    protected static BlockState parseBlockState(String s) {
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
