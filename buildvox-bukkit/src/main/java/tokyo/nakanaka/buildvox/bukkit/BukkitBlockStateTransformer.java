package tokyo.nakanaka.buildvox.bukkit;

import org.bukkit.Axis;
import org.bukkit.Server;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.*;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.blockStateTransformer.BlockStateTransformer;
import tokyo.nakanaka.buildvox.core.blockStateTransformer.BlockTransformation;
import tokyo.nakanaka.buildvox.core.math.transformation.*;
import tokyo.nakanaka.buildvox.core.math.vector.*;
import tokyo.nakanaka.buildvox.core.world.Block;

import java.util.*;

import static org.bukkit.block.BlockFace.*;

/**
 * The class which implements {@link BlockStateTransformer} for Bukkit Platform
 */
public class BukkitBlockStateTransformer implements BlockStateTransformer {
    private static final Logger LOGGER = LoggerFactory.getLogger(BukkitBlockStateTransformer.class);
    private Server server;
    public BukkitBlockStateTransformer(Server server) {
        this.server = server;
    }

    @Override
    public Map<String, String> transform(NamespacedId blockId, Map<String, String> stateMap, BlockTransformation blockTrans){
        Matrix3x3i transMatrix = blockTrans.toMatrix3x3i();
        String blockStr = new Block(blockId, stateMap).toString();
        BlockData blockData;
        try {
            blockData = server.createBlockData(blockStr);
        }catch (IllegalArgumentException e){
            LOGGER.warn("Unexpected combination of blockId and stateMap");
            return stateMap;
        }
        //TODO Attachable
        if(blockData instanceof Bisected bisected){
            blockData = transformBisected(bisected, transMatrix);
        }
        if(blockData instanceof Directional directional){
            blockData = transformDirectional(directional, transMatrix);
        }
        //TODO FaceAttachable
        if(blockData instanceof MultipleFacing multipleFacing){
            blockData = transformMultipleFacing(multipleFacing, transMatrix);
        }
        if(blockData instanceof Orientable orientable){
            blockData = transformOrientable(orientable, transMatrix);
        }
        //TODO Rail
        if(blockData instanceof Rotatable rotatable){
            blockData = transformRotatable(rotatable, transMatrix);
        }
        //From here Specific BlockData org.bukkit.block.data.type
        if(blockData instanceof Slab slab){
            blockData = transformSlab(slab, transMatrix);
        }
        if(blockData instanceof Stairs stairs){
            blockData = transformStairsShape(stairs, transMatrix);
        }
        return Block.valueOf(blockData.getAsString()).getStateMap();
    }

    private Bisected transformBisected(Bisected bisected, Matrix3x3i transMatrix){
        Vector3i direction;
        Bisected.Half half = bisected.getHalf();
        switch (half){
            case TOP -> direction = Vector3i.PLUS_J;
            case BOTTOM -> direction = Vector3i.MINUS_J;
            default -> {
                LOGGER.warn("Unexpected Bisected half: " + half);
                return bisected;
            }
        }
        Bisected transBisected = (Bisected) bisected.clone();
        int transDirectionY = transMatrix.apply(direction).y();
        if (transDirectionY == -1) {
            transBisected.setHalf(Bisected.Half.BOTTOM);
        } else if(transDirectionY == 1){
            transBisected.setHalf(Bisected.Half.TOP);
        }
        return transBisected;
    }

    private Directional transformDirectional(Directional directional, Matrix3x3i transMatrix) {
        BlockFace blockFace = directional.getFacing();
        if(!blockFace.isCartesian()) {
            return directional;
        }
        Vector3i vector;
        try{
            vector = CartesianBlockFaceHelper.getVectorOf(blockFace);
        }catch (IllegalArgumentException e){
            LOGGER.warn("Unexpected blockFace :" + blockFace);
            return directional;
        }
        Vector3i transVector = transMatrix.apply(vector);
        BlockFace transBlockFace= CartesianBlockFaceHelper.getBlockFaceOf(transVector);
        if(directional.getFaces().contains(transBlockFace)){
            Directional transDirectional = (Directional) directional.clone();
            transDirectional.setFacing(transBlockFace);
            return transDirectional;
        }else {
            return directional;
        }
    }

    private MultipleFacing transformMultipleFacing(MultipleFacing multipleFacing, Matrix3x3i transMatrix){
        MultipleFacing transMultipleFacing = (MultipleFacing) multipleFacing.clone();
        Set<BlockFace> allowedFaces = multipleFacing.getAllowedFaces();
        Set<BlockFace> originalFaces = new HashSet<>();
        for(BlockFace f: allowedFaces){
            if(multipleFacing.hasFace(f)) {
                originalFaces.add(f);
            }
            transMultipleFacing.setFace(f, false);
        }
        for(BlockFace blockFace : originalFaces) {
            Vector3i vector = CartesianBlockFaceHelper.getVectorOf(blockFace);
            Vector3i transVector = transMatrix.apply(vector);
            BlockFace transFace = CartesianBlockFaceHelper.getBlockFaceOf(transVector);
            if(allowedFaces.contains(transFace)) {
                transMultipleFacing.setFace(transFace, true);
            }
        }
        return transMultipleFacing;
    }

    private Orientable transformOrientable(Orientable orientable, Matrix3x3i matrix3x3i) {
        Axis axis = orientable.getAxis();
        Vector3i vector;
        switch (axis){
            case X -> vector = Vector3i.PLUS_I;
            case Y -> vector = Vector3i.PLUS_J;
            case Z -> vector = Vector3i.PLUS_K;
            default -> {
                LOGGER.warn("Unexpected axis:" + axis);
                return orientable;
            }
        }
        Axis transAxis;
        Vector3i transVector = matrix3x3i.apply(vector);
        if(transVector.equals(Vector3i.PLUS_I) || transVector.equals(Vector3i.MINUS_I)) {
            transAxis = Axis.X;
        }else if(transVector.equals(Vector3i.PLUS_J) || transVector.equals(Vector3i.MINUS_J)) {
            transAxis = Axis.Y;
        }else if(transVector.equals(Vector3i.PLUS_K) || transVector.equals(Vector3i.MINUS_K)) {
            transAxis = Axis.Z;
        }else{
            LOGGER.warn("Unexpected transVector :" + transVector);
            return orientable;
        }
        Set<Axis> applicableAxisSet = orientable.getAxes();
        Orientable transOrientable = (Orientable) orientable.clone();
        if(applicableAxisSet.contains(transAxis)) {
            transOrientable.setAxis(transAxis);
        }
        return transOrientable;
    }

    private Rotatable transformRotatable(Rotatable rotatable, Matrix3x3i transMatrix) {
        Vector3i transJ = transMatrix.apply(Vector3i.PLUS_J);
        if(!transJ.equals(Vector3i.PLUS_J) && !transJ.equals(Vector3i.MINUS_J)) {
            return rotatable;
        }
        Matrix3x3d matrixD = transMatrix.toMatrix3x3d();
        BlockFace rotation = rotatable.getRotation();
        Vector3d vector;
        try{
            vector = RotationHelper.getVector3dOf(rotation);
        }catch (IllegalArgumentException e){
            LOGGER.warn("Unexpected rotation :" + rotation);
            return rotatable;
        }
        BlockFace transRotation;
        Vector3d transVector = matrixD.apply(vector);
        try{
            transRotation = RotationHelper.getRotationOf(transVector);
        }catch (IllegalArgumentException e){
            LOGGER.warn("Unexpected transVector :" + transVector);
            return rotatable;
        }
        Rotatable transRotatable = (Rotatable) rotatable.clone();
        transRotatable.setRotation(transRotation);
        return transRotatable;
    }

    private Slab transformSlab(Slab slab, Matrix3x3i transMatrix) {
        Vector3i dir;
        Slab.Type type = slab.getType();
        switch (type){
            case TOP -> dir = Vector3i.PLUS_J;
            case BOTTOM -> dir = Vector3i.MINUS_J;
            default -> {
                LOGGER.warn("Unexpected Bisected half: " + type);
                return slab;
            }
        }
        Slab transSlab = (Slab) slab.clone();
        int transDirY = transMatrix.apply(dir).y();
        if (transDirY == -1) {
            transSlab.setType(Slab.Type.BOTTOM);
        } else if(transDirY == 1){
            transSlab.setType(Slab.Type.TOP);
        }
        return transSlab;
    }

    private Stairs transformStairsShape(Stairs stairs, Matrix3x3i transMatrix) {
        Vector3i transI = transMatrix.apply(Vector3i.PLUS_I);
        Vector3i transJ = transMatrix.apply(Vector3i.PLUS_J);
        Vector3i transK = transMatrix.apply(Vector3i.PLUS_K);
        if(!transJ.equals(Vector3i.PLUS_J) && !transJ.equals(Vector3i.MINUS_J)) {
            return stairs;
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
            return stairs;
        }else{
            Stairs transStairs = (Stairs) stairs.clone();
            Stairs.Shape shape = stairs.getShape();
            switch (shape) {
                case INNER_LEFT -> transStairs.setShape(Stairs.Shape.INNER_RIGHT);
                case INNER_RIGHT -> transStairs.setShape(Stairs.Shape.INNER_LEFT);
                case OUTER_LEFT -> transStairs.setShape(Stairs.Shape.OUTER_RIGHT);
                case OUTER_RIGHT -> transStairs.setShape(Stairs.Shape.OUTER_LEFT);
            }
            return transStairs;
        }
    }

    private static class CartesianBlockFaceHelper {
        private static Map<Vector3i, BlockFace> blockFaceMap = new HashMap<>();
        static {
            blockFaceMap.put(Vector3i.PLUS_I, EAST);
            blockFaceMap.put(Vector3i.MINUS_I, WEST);
            blockFaceMap.put(Vector3i.PLUS_J, UP);
            blockFaceMap.put(Vector3i.MINUS_J, DOWN);
            blockFaceMap.put(Vector3i.PLUS_K, SOUTH);
            blockFaceMap.put(Vector3i.MINUS_K, NORTH);
        }

        /**
         * @throws IllegalArgumentException if blockFace is not cartesian(north, south, east, west, up, down)
         */
        public static Vector3i getVectorOf(BlockFace blockFace) {
            for(var e : blockFaceMap.entrySet()) {
                if(e.getValue() == blockFace) {
                    return e.getKey();
                }
            }
            throw new IllegalArgumentException("not cartesian(north, south, east, west, up, down)");
        }

        /**
         * @throws IllegalArgumentException if the given vector is not convertible to BlockFace.
         */
        public static BlockFace getBlockFaceOf(Vector3i vector) {
            BlockFace blockFace = blockFaceMap.get(vector);
            if(blockFace == null)throw new IllegalArgumentException();
            return blockFace;
        }
    }

    private static class RotationHelper {
        private static Map<Vector3d, BlockFace> rotationMap = new HashMap<>();
        static {
            rotationMap.put(Vector3d.PLUS_I, EAST);
            rotationMap.put(Vector3d.MINUS_I, WEST);
            rotationMap.put(Vector3d.PLUS_K, SOUTH);
            rotationMap.put(Vector3d.MINUS_K, NORTH);
            Vector3d ne = new Vector3d(1, 0, -1).scalarMultiply(1.0/Math.sqrt(2));
            rotationMap.put(ne, NORTH_EAST);
            Vector3d nw = new Vector3d(-1, 0, -1).scalarMultiply(1.0/Math.sqrt(2));
            rotationMap.put(nw, NORTH_WEST);
            Vector3d se = new Vector3d(1, 0, 1).scalarMultiply(1.0/Math.sqrt(2));
            rotationMap.put(se, SOUTH_EAST);
            Vector3d sw = new Vector3d(-1, 0, 1).scalarMultiply(1.0/Math.sqrt(2));
            rotationMap.put(sw, SOUTH_WEST);
            Vector3d nne = AffineTransformation3d.ofRotationY(-Math.PI/8).apply(Vector3d.MINUS_K);
            rotationMap.put(nne, NORTH_NORTH_EAST);
            Vector3d nnw = AffineTransformation3d.ofRotationY(Math.PI/8).apply(Vector3d.MINUS_K);
            rotationMap.put(nnw, NORTH_NORTH_WEST);
            Vector3d sse = AffineTransformation3d.ofRotationY(Math.PI/8).apply(Vector3d.PLUS_K);
            rotationMap.put(sse, SOUTH_SOUTH_EAST);
            Vector3d ssw = AffineTransformation3d.ofRotationY(-Math.PI/8).apply(Vector3d.PLUS_K);
            rotationMap.put(ssw, SOUTH_SOUTH_WEST);
            Vector3d ene = AffineTransformation3d.ofRotationY(Math.PI/8).apply(Vector3d.PLUS_I);
            rotationMap.put(ene, EAST_NORTH_EAST);
            Vector3d ese = AffineTransformation3d.ofRotationY(-Math.PI/8).apply(Vector3d.PLUS_I);
            rotationMap.put(ese, EAST_SOUTH_EAST);
            Vector3d wnw = AffineTransformation3d.ofRotationY(-Math.PI/8).apply(Vector3d.MINUS_I);
            rotationMap.put(wnw, WEST_NORTH_WEST);
            Vector3d wsw = AffineTransformation3d.ofRotationY(Math.PI/8).apply(Vector3d.MINUS_I);
            rotationMap.put(wsw, WEST_SOUTH_WEST);
        }
        public static Vector3d getVector3dOf(BlockFace rotation) {
            for(var e : rotationMap.entrySet()) {
                if(e.getValue() == rotation) {
                    return e.getKey();
                }
            }
            throw new IllegalArgumentException("not rotation");
        }
        public static BlockFace getRotationOf(Vector3d vector) {
            if(vector.y() != 0) {
                throw new IllegalArgumentException("vector y component must be 0");
            }
            for(var e : rotationMap.entrySet()) {
                if(e.getKey().subtract(vector).length() < 0.1) {
                    return e.getValue();
                }
            }
            throw new IllegalArgumentException("invalid vector");
        }
    }

    @SuppressWarnings("unused")
    private static class HelperOld {
        private static final Map<BlockFace, Vector3d> faceVectorMap = new HashMap<>();
        private static final Map<Vector3d, BlockFace> vectorFaceMap = new HashMap<>();

        static{
            faceVectorMap.put(NORTH, Vector3d.MINUS_K);
            vectorFaceMap.put(Vector3d.MINUS_K, NORTH);
            faceVectorMap.put(SOUTH, Vector3d.PLUS_K);
            vectorFaceMap.put(Vector3d.PLUS_K, SOUTH);
            faceVectorMap.put(EAST, Vector3d.PLUS_I);
            vectorFaceMap.put(Vector3d.PLUS_I, EAST);
            faceVectorMap.put(WEST, Vector3d.MINUS_I);
            vectorFaceMap.put(Vector3d.MINUS_I, WEST);
            faceVectorMap.put(UP, Vector3d.PLUS_J);
            vectorFaceMap.put(Vector3d.PLUS_J, UP);
            faceVectorMap.put(DOWN, Vector3d.MINUS_J);
            vectorFaceMap.put(Vector3d.MINUS_J, DOWN);

            Vector3d ne = new Vector3d(1, 0, -1).scalarMultiply(1.0/Math.sqrt(2));
            faceVectorMap.put(NORTH_EAST, ne);
            vectorFaceMap.put(ne, NORTH_EAST);
            Vector3d nw = new Vector3d(-1, 0, -1).scalarMultiply(1.0/Math.sqrt(2));
            faceVectorMap.put(NORTH_WEST, nw);
            vectorFaceMap.put(nw, NORTH_WEST);
            Vector3d se = new Vector3d(1, 0, 1).scalarMultiply(1.0/Math.sqrt(2));
            faceVectorMap.put(SOUTH_EAST, se);
            vectorFaceMap.put(se, SOUTH_EAST);
            Vector3d sw = new Vector3d(-1, 0, 1).scalarMultiply(1.0/Math.sqrt(2));
            faceVectorMap.put(SOUTH_WEST, sw);
            vectorFaceMap.put(sw, SOUTH_WEST);

            Vector3d nne = AffineTransformation3d.ofRotationY(-Math.PI/8).apply(Vector3d.MINUS_K);
            faceVectorMap.put(NORTH_NORTH_EAST, nne);
            vectorFaceMap.put(nne, NORTH_NORTH_EAST);
            Vector3d nnw = AffineTransformation3d.ofRotationY(Math.PI/8).apply(Vector3d.MINUS_K);
            faceVectorMap.put(NORTH_NORTH_WEST, nnw);
            vectorFaceMap.put(nnw, NORTH_NORTH_WEST);

            Vector3d sse = AffineTransformation3d.ofRotationY(Math.PI/8).apply(Vector3d.PLUS_K);
            faceVectorMap.put(SOUTH_SOUTH_EAST, sse);
            vectorFaceMap.put(sse, SOUTH_SOUTH_EAST);
            Vector3d ssw = AffineTransformation3d.ofRotationY(-Math.PI/8).apply(Vector3d.PLUS_K);
            faceVectorMap.put(SOUTH_SOUTH_WEST, ssw);
            vectorFaceMap.put(ssw, SOUTH_SOUTH_WEST);

            Vector3d ene = AffineTransformation3d.ofRotationY(Math.PI/8).apply(Vector3d.PLUS_I);
            faceVectorMap.put(EAST_NORTH_EAST, ene);
            vectorFaceMap.put(ene, EAST_NORTH_EAST);
            Vector3d ese = AffineTransformation3d.ofRotationY(-Math.PI/8).apply(Vector3d.PLUS_I);
            faceVectorMap.put(EAST_SOUTH_EAST, ese);
            vectorFaceMap.put(ese, EAST_SOUTH_EAST);

            Vector3d wnw = AffineTransformation3d.ofRotationY(-Math.PI/8).apply(Vector3d.MINUS_I);
            faceVectorMap.put(WEST_NORTH_WEST, wnw);
            vectorFaceMap.put(wnw, WEST_NORTH_WEST);
            Vector3d wsw = AffineTransformation3d.ofRotationY(Math.PI/8).apply(Vector3d.MINUS_I);
            faceVectorMap.put(WEST_SOUTH_WEST, wsw);
            vectorFaceMap.put(wsw, WEST_SOUTH_WEST);
        }

        private static Vector3d getNearestVector(Vector3d v, Vector3d... candidates){
            if(candidates.length == 0)throw new IllegalArgumentException();
            Vector3d nearest = candidates[0];
            double
                    dis = v.distance(candidates[0]);
            for(Vector3d c : candidates){
                double disVc = v.distance(c);
                if(disVc < dis){
                    nearest = c;
                    dis = disVc;
                }
            }
            return nearest;
        }

        private static Vector3d[] getBlockFaceVectors(Set<BlockFace> s){
            List<Vector3d> list = new ArrayList<>();
            BlockFace[] blockFaces = new BlockFace[]{NORTH, SOUTH, EAST, WEST, UP, DOWN,
                    NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST,
                    NORTH_NORTH_EAST, NORTH_NORTH_WEST, SOUTH_SOUTH_EAST, SOUTH_SOUTH_WEST,
                    EAST_NORTH_EAST, EAST_SOUTH_EAST, WEST_NORTH_WEST, WEST_SOUTH_WEST};
            for(var face : blockFaces){
                if(s.contains(face))list.add(faceVectorMap.get(face));
            }
            return list.toArray(new Vector3d[0]);
        }

    }

}
