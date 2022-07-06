package tokyo.nakanaka.buildvox.fabric.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.enums.StairShape;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.block.*;
import tokyo.nakanaka.buildvox.core.math.transformation.Matrix3x3i;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;

import java.util.HashMap;
import java.util.Map;

/*
 * internal
 * Block implementation class for Fabric platform.
 */
public class FabricBlock implements Block<StateImpl, EntityImpl> {
    private NamespacedId id;
    private FabricBlockStateTransformer stateTransformer = new FabricBlockStateTransformer();

    public FabricBlock(NamespacedId id) {
        this.id = id;
    }

    @Override
    public NamespacedId getId() {
        return id;
    }

    @Override
    public StateImpl transformState(StateImpl state, BlockTransformation trans) {
        Map<String, String> transMap = stateTransformer.transform(id, state.getStateMap(), trans);
        return new StateImpl(transMap);
    }

    public StateImpl parseState(String s) {
        return StateImpl.valueOf(s);
    }

    private static class FabricBlockStateTransformer {
        public Map<String, String> transform(NamespacedId blockId, Map<String, String> stateMap, BlockTransformation blockTrans) {
            Matrix3x3i transMatrix = blockTrans.toMatrix3x3i();
            VoxelBlock block = new VoxelBlock(blockId, new StateImpl(stateMap));
            BlockState blockState = BlockUtils.createBlockState(block);
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
            return BlockUtils.createStateImpl(transState).getStateMap();
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
}
