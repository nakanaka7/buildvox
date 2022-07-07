package tokyo.nakanaka.buildvox.fabric.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.enums.StairShape;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import tokyo.nakanaka.buildvox.core.block.Block;
import tokyo.nakanaka.buildvox.core.block.BlockTransformation;
import tokyo.nakanaka.buildvox.core.math.transformation.Matrix3x3i;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;

import java.util.*;

/* internal */
public class FabricBlockState implements Block.State {
    private final BlockState blockState;
    private final Map<String, String> stateMap;

    public FabricBlockState(BlockState blockState) {
        this.blockState = blockState;
        Collection<Property<?>> properties0 = blockState.getProperties();
        Map<String, String> stateMap = new HashMap<>();
        for(var key0 : properties0){
            Object value0 = blockState.get(key0);
            stateMap.put(key0.getName().toLowerCase(), value0.toString().toLowerCase());
        }
        this.stateMap = stateMap;
    }

    public Map<String, String> getStateMap() {
        return stateMap;
    }

    static BlockState transform(BlockState blockState, BlockTransformation blockTrans) {
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

    private static Map<String, String> transformStairsShape(Map<String, String> stateMap, Matrix3x3i transMatrix) {
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

    @Override
    public String toString() {
        Set<String> keyValueSet = new HashSet<>();
        for (Map.Entry<String, String> e : stateMap.entrySet()) {
            keyValueSet.add(e.getKey() + "=" + e.getValue());
        }
        return String.join(",", keyValueSet);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FabricBlockState state = (FabricBlockState) o;
        return stateMap.equals(state.stateMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stateMap);
    }

}
