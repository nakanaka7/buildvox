package tokyo.nakanaka.buildvox.fabric.block;

import net.minecraft.block.enums.StairShape;
import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.math.transformation.Matrix3x3i;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;

import java.util.HashMap;
import java.util.Map;

public class StairsBlock extends FabricBlock {
    public StairsBlock(NamespacedId id) {
        super(id);
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
