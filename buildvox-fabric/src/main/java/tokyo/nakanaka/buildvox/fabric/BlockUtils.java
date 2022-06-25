package tokyo.nakanaka.buildvox.fabric;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.StairShape;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.block.*;
import tokyo.nakanaka.buildvox.core.math.transformation.Matrix3x3i;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;
import tokyo.nakanaka.buildvox.core.world.VoxelBlock;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BlockUtils {
    private BlockUtils() {
    }

    public static void registerBlocks() {
        for(Identifier blockId0 : Registry.BLOCK.getIds()) {
            NamespacedId id = new NamespacedId(blockId0.getNamespace(), blockId0.getPath());
            BuildVoxSystem.getBlockRegistry().register(new BlockImpl(id, new FabricBlockStateTransformer()));
        }
    }

    public static VoxelBlock getVoxelBlock(net.minecraft.block.BlockState blockState, BlockEntity blockEntity) {
        net.minecraft.block.Block block0 = blockState.getBlock();
        Identifier id0 = Registry.BLOCK.getId(block0);
        NamespacedId id = new NamespacedId(id0.getNamespace(), id0.getPath());
        BlockImpl block = new BlockImpl(id, new FabricBlockStateTransformer());
        StateImpl state = convertToStateImpl(blockState);
        EntityImpl entity = null;
        if(blockEntity != null) {
            NbtCompound nbt = blockEntity.createNbtWithId();
            entity = new EntityImpl(nbt);
        }
        return new VoxelBlock(block.getId(), state, entity);
    }

    public static StateImpl convertToStateImpl(BlockState blockState) {
        Collection<Property<?>> properties0 = blockState.getProperties();
        Map<String, String> stateMap = new HashMap<>();
        for(var key0 : properties0){
            Object value0 = blockState.get(key0);
            stateMap.put(key0.getName().toLowerCase(), value0.toString().toLowerCase());
        }
        return new StateImpl(stateMap);
    }

    public record StateEntity(BlockState state, BlockEntity entity) {
    }

    public static StateEntity getBlockStateEntity(int x, int y, int z, VoxelBlock block) {
        String blockStr = block.withoutEntity().toString();
        var state = parseBlockState(blockStr);
        var entity = createBlockEntity(x, y, z, block, state);
        return new StateEntity(state, entity);
    }

    private static BlockEntity createBlockEntity(int x, int y, int z, VoxelBlock block, BlockState blockState) {
        EntityImpl entity = (EntityImpl) block.getEntity();
        if(entity == null) {
            return null;
        }
        NbtCompound nbt = (NbtCompound) entity.getObj();
        return BlockEntity.createFromNbt(new BlockPos(x, y, z), blockState, nbt);
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

    /**
     * The implementation of {@link BlockStateTransformer} for Fabric platform
     */
    public static class FabricBlockStateTransformer implements BlockStateTransformer {

        @Override
        public Map<String, String> transform(NamespacedId blockId, Map<String, String> stateMap, BlockTransformation blockTrans) {
            Matrix3x3i transMatrix = blockTrans.toMatrix3x3i();
            String blockStr = new VoxelBlock(blockId, new StateImpl(stateMap)).toString();
            BlockState blockState = parseBlockState(blockStr);
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
            VoxelBlock transBlock = getVoxelBlock(transState);
            return ((StateImpl)transBlock.getState()).getStateMap();
        }

        public static VoxelBlock getVoxelBlock(BlockState blockState) {
            net.minecraft.block.Block block0 = blockState.getBlock();
            Identifier id0 = Registry.BLOCK.getId(block0);
            NamespacedId id = new NamespacedId(id0.getNamespace(), id0.getPath());
            var stateMap = convertToStateImpl(blockState).getStateMap();
            return new VoxelBlock(id, new StateImpl(stateMap));
        }

        private Map<String, String> transformStairsShape(Map<String, String> stateMap, Matrix3x3i transMatrix) {
            Vector3i transI = transMatrix.apply(Vector3i.PLUS_I);
            Vector3i transJ = transMatrix.apply(Vector3i.PLUS_J);
            Vector3i transK = transMatrix.apply(Vector3i.PLUS_K);
            if(!transJ.equals(Vector3i.PLUS_J) && !transJ.equals(Vector3i.MINUS_J)) {
                return stateMap;
            }
            int ix = transI.x();
            int iy = transI.y();
            int iz = transI.z();
            int jx = transJ.x();
            int jy = transJ.y();
            int jz = transJ.z();
            int kx0 = iy * jz - iz * jy;
            int ky0 = iz * jx - ix * jz;
            int kz0 = ix * jy - iy * jx;
            Vector3i transIxTransJ = new Vector3i(kx0, ky0, kz0);
            if((transJ.equals(Vector3i.PLUS_J) && transIxTransJ.equals(transK))
                    || (transJ.equals(Vector3i.MINUS_J) && !transIxTransJ.equals(transK))){
                return stateMap;
            }else{
                Map<String, String> transStateMap = new HashMap<>(stateMap);
                String shapeKey = StairsBlock.SHAPE.getName().toLowerCase();
                String shapeValue = stateMap.get(shapeKey);
                if(shapeValue == null){
                    return stateMap;
                }
                shapeValue = shapeValue.toLowerCase();
                if(shapeValue.equals(StairShape.INNER_LEFT.asString())){
                    transStateMap.put(shapeKey, StairShape.INNER_RIGHT.asString());
                }else if(shapeValue.equals(StairShape.INNER_RIGHT.asString())) {
                    transStateMap.put(shapeKey, StairShape.INNER_LEFT.asString());
                }else if(shapeValue.equals(StairShape.OUTER_LEFT.asString())) {
                    transStateMap.put(shapeKey, StairShape.OUTER_RIGHT.asString());
                }else if(shapeValue.equals(StairShape.OUTER_RIGHT.asString())) {
                    transStateMap.put(shapeKey, StairShape.OUTER_LEFT.asString());
                }
                return transStateMap;
            }
        }

    }

    /**
     * The implementation of BlockValidator for Fabric platform
     */
    public static class FabricBlockValidator implements BlockValidator {
        @Override
        public boolean validate(VoxelBlock block) {
            String blockStr = block.withoutEntity().toString();
            try{
                parseBlockState(blockStr);
            }catch (IllegalArgumentException e){
                return false;
            }
            return true;
        }
    }

}
