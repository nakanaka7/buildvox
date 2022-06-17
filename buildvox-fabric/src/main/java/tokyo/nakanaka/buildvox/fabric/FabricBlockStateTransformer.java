package tokyo.nakanaka.buildvox.fabric;

import net.minecraft.block.StairsBlock;
import net.minecraft.block.enums.StairShape;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.block.BlockStateTransformer;
import tokyo.nakanaka.buildvox.core.block.BlockTransformation;
import tokyo.nakanaka.buildvox.core.block.StateImpl;
import tokyo.nakanaka.buildvox.core.math.transformation.Matrix3x3i;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.world.VoxelBlock;

import java.util.HashMap;
import java.util.Map;

/**
 * The implementation of {@link BlockStateTransformer} for Fabric platform
 */
public class FabricBlockStateTransformer implements BlockStateTransformer {

    @Override
    public Map<String, String> transform(NamespacedId blockId, Map<String, String> stateMap, BlockTransformation blockTrans) {
        Matrix3x3i transMatrix = blockTrans.toMatrix3x3i();
        String blockStr = new VoxelBlock(blockId, new StateImpl(stateMap)).toString();
        net.minecraft.block.BlockState blockState = BlockUtils.parseBlockState(blockStr);
        Vector3i transI = transMatrix.apply(Vector3i.PLUS_I);
        Vector3i transJ = transMatrix.apply(Vector3i.PLUS_J);
        Vector3i transK = transMatrix.apply(Vector3i.PLUS_K);
        net.minecraft.block.BlockState transState;
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

    public static VoxelBlock getVoxelBlock(net.minecraft.block.BlockState blockState) {
        net.minecraft.block.Block block0 = blockState.getBlock();
        Identifier id0 = Registry.BLOCK.getId(block0);
        NamespacedId id = new NamespacedId(id0.getNamespace(), id0.getPath());
        var stateMap = FabricVoxelBlock.convertToStateImpl(blockState).getStateMap();
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
