package tokyo.nakanaka.buildvox.fabric.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.enums.StairShape;
import net.minecraft.util.math.Direction;
import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.block.BlockTransformation;
import tokyo.nakanaka.buildvox.core.block.StateImpl;
import tokyo.nakanaka.buildvox.core.math.transformation.Matrix3x3i;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;

import java.util.HashMap;
import java.util.Map;

public class StairsFabricBlock extends FabricBlock {
    public StairsFabricBlock(NamespacedId id) {
        super(id);
    }
    @Override
    public FabricBlockState transformState(FabricBlockState state, BlockTransformation blockTrans) {
        if(isRotation(blockTrans)) {
            return super.transformState(state, blockTrans);
        }
        BlockState blockState = state.getBlockState();
        BlockState transBlockState = changeFacing(blockState, blockTrans);
        Matrix3x3i m = blockTrans.toMatrix3x3i();
        Vector3i transJ = m.apply(Vector3i.PLUS_J);
        if(transJ.equals(Vector3i.PLUS_J)) {
            transBlockState = flipLeftRight(transBlockState);
        }
        return new FabricBlockState(transBlockState);
    }

    private static boolean isRotation(BlockTransformation blockTrans) {
        Matrix3x3i matrix = blockTrans.toMatrix3x3i();
        return matrix.determinant() == 1;
    }

    private static BlockState changeFacing(BlockState blockState, BlockTransformation blockTrans) {
        Direction facing = blockState.get(StairsBlock.FACING);
        Vector3i v = switch (facing) {
            case NORTH -> Vector3i.MINUS_K;
            case SOUTH -> Vector3i.PLUS_K;
            case EAST -> Vector3i.PLUS_I;
            case WEST -> Vector3i.MINUS_I;
            default -> Vector3i.PLUS_K;
        };
        Matrix3x3i matrix = blockTrans.toMatrix3x3i();
        Vector3i w = matrix.apply(v);
        Direction g;
        if(w.equals(Vector3i.MINUS_K)) {
            g = Direction.NORTH;
        }else if(w.equals(Vector3i.PLUS_K)) {
            g = Direction.SOUTH;
        }else if(w.equals(Vector3i.PLUS_I)) {
            g = Direction.EAST;
        } else if (w.equals(Vector3i.MINUS_I)) {
            g = Direction.WEST;
        }else {
            g = facing;
        }
        return blockState.with(StairsBlock.FACING, g);
    }

    private static BlockState flipLeftRight(BlockState blockState) {
        StairShape shape = blockState.get(StairsBlock.SHAPE);
        return switch (shape) {
            case INNER_LEFT -> blockState.with(StairsBlock.SHAPE, StairShape.INNER_RIGHT);
            case OUTER_LEFT -> blockState.with(StairsBlock.SHAPE, StairShape.OUTER_RIGHT);
            case INNER_RIGHT -> blockState.with(StairsBlock.SHAPE, StairShape.INNER_LEFT);
            case OUTER_RIGHT -> blockState.with(StairsBlock.SHAPE, StairShape.OUTER_LEFT);
            default -> blockState;
        };
    }

    private FabricBlockState transformStateOld(FabricBlockState state, BlockTransformation blockTrans) {
        Map<String, String> stateMap = state.getStateMap();
        Map<String, String> transStateMap = transformStairsShape(stateMap, blockTrans.toMatrix3x3i());
        String s = new StateImpl(transStateMap).toString();
        String t = id + "[" + s +"]";
        BlockState blockState = parseBlockState(t);
        return new FabricBlockState(blockState);
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
            String shapeKey = net.minecraft.block.StairsBlock.SHAPE.getName().toLowerCase();
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
