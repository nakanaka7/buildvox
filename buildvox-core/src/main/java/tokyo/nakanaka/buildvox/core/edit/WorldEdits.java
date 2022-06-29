package tokyo.nakanaka.buildvox.core.edit;

import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.block.BlockTransformation;
import tokyo.nakanaka.buildvox.core.block.StateImpl;
import tokyo.nakanaka.buildvox.core.edit.clientWorld.BlockTransformingClientWorld;
import tokyo.nakanaka.buildvox.core.edit.clientWorld.ClientWorld;
import tokyo.nakanaka.buildvox.core.edit.voxelSpace.VoxelSpace;
import tokyo.nakanaka.buildvox.core.math.region3d.Cuboid;
import tokyo.nakanaka.buildvox.core.math.transformation.AffineTransformation3d;
import tokyo.nakanaka.buildvox.core.math.transformation.Matrix3x3i;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.selection.Selection;
import tokyo.nakanaka.buildvox.core.world.VoxelBlock;
import tokyo.nakanaka.buildvox.core.world.World;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The utility class of world edits.
 */
public class WorldEdits {
    private WorldEdits() {
    }

    /**
     * Copies the blocks in selection. A coordinate system is made which origin is the pos.
     * Each coordinate axis is divided by length 1, and the space is full by 1x1x1 cubes as a result. If the center of
     * the cube is contained in the selection, the cube is copy target. The copy block type is
     * the type of the center, and the block position in the clipboard is the same position as the made coordinate
     * system(Not original world coordinate system).
     * @param srcWorld the source world.
     * @param srcSel the source selection.
     * @param pos the position of the source which corresponds to the origin of the clipboard.
     * @param dest the destination clipboard.
     */
    public static void copy(ClientWorld srcWorld, Selection srcSel, Vector3d pos, Clipboard dest) {
        var trans = AffineTransformation3d.ofTranslation(-pos.x(), -pos.y(), -pos.z());
        VoxelSpaceEdits.copy(srcWorld, srcSel.calculateBlockPosSet(), dest, trans);
    }

    /**
     * Copies the blocks of the source selection into the destination clipboard. The pos of the selection corresponds to
     * the origin of the clipboard.
     * @param srcWorld the source world.
     * @param srcSel the source selection.
     * @param pos the position of the source which corresponds to the origin of the clipboard.
     * @param dest the destination clipboard.
     */
    public static void copy(World srcWorld, Selection srcSel, Vector3d pos, Clipboard dest) {
        var trans = AffineTransformation3d.ofTranslation(-pos.x(), -pos.y(), -pos.z());
        VoxelSpaceEdits.copy(new ClientWorld(srcWorld), srcSel.calculateBlockPosSet(), dest, trans);
    }

    /**
     * Pastes the clipboard contents into the destination world.
     * @param srcClip the source clipboard.
     * @param dest the destination world.
     * @param pos the position of the world which corresponds to the origin of the clipboard.
     */
    public static void paste(Clipboard srcClip, ClientWorld dest, Vector3d pos) {
        paste(srcClip, dest, pos, AffineTransformation3d.IDENTITY);
    }

    /**
     * Pastes the clipboard contents into the destination world.
     * @param srcClip the source clipboard.
     * @param dest the destination world.
     * @param pos the position of the world which corresponds to the origin of the clipboard.
     * @param clipboardTrans the clipboard transformation
     */
    public static void paste(Clipboard srcClip, ClientWorld dest, Vector3d pos, AffineTransformation3d clipboardTrans) {
        Set<Vector3i> srcPosSet = srcClip.blockPosSet();
        BlockTransformation blockTrans = BlockTransformApproximator.approximateToBlockTrans(clipboardTrans);
        VoxelSpace<VoxelBlock> transDest = new BlockTransformingClientWorld(blockTrans, dest);
        AffineTransformation3d trans = AffineTransformation3d.ofTranslation(pos.x(), pos.y(), pos.z()).compose(clipboardTrans);
        VoxelSpaceEdits.copy(srcClip, srcPosSet, transDest, trans);
    }

    /**
     * Fill the block in the selection.
     * @param world the world.
     * @param sel the selection.
     * @param block the block.
     */
    public static void fill(ClientWorld world, Selection sel, VoxelBlock block) {
        VoxelSpaceEdits.fill(world, sel.calculateBlockPosSet(), block);
    }

    /**
     * Replaces the blocks in the selection.
     * @param world the world
     * @param sel the selection
     * @param fromBlock the block type to be replaced from.
     * @param toBlock the block type to be replaced to.
     */
    public static void replace(ClientWorld world, Selection sel, VoxelBlock fromBlock, VoxelBlock toBlock) {
        NamespacedId fromId = fromBlock.getBlockId();
        Map<String, String> fromStateMap = ((StateImpl) fromBlock.getState()).getStateMap();
        VoxelSpaceEdits.replace(world, sel.calculateBlockPosSet(),
                (block) -> {
                    NamespacedId id = block.getBlockId();
                    Map<String, String> stateMap = ((StateImpl) block.getState()).getStateMap();
                    if (!fromId.equals(id)) return false;
                    for (Map.Entry<String, String> entry : fromStateMap.entrySet()) {
                        String value = stateMap.get(entry.getKey());
                        if (!value.equals(entry.getValue())) return false;
                    }
                    return true;
                },
                toBlock);
    }

    /*
     * Repeats the blocks in the cuboid specified by pos0 and pos1.
     * @param world the world
     * @param pos0 the position of a corner.
     * @param pos1 the position of another corner.
     * @param countX the count along x-axis.
     * @param countY the count along y-axis.
     * @param countZ the count along z-axis.
     */
    public static void repeatOld(ClientWorld world, Vector3i pos0, Vector3i pos1,
                                 int countX, int countY, int countZ) {
        int maxX = Math.max(pos0.x(), pos1.x());
        int maxY = Math.max(pos0.y(), pos1.y());
        int maxZ = Math.max(pos0.z(), pos1.z());
        int minX = Math.min(pos0.x(), pos1.x());
        int minY = Math.min(pos0.y(), pos1.y());
        int minZ = Math.min(pos0.z(), pos1.z());
        Cuboid cuboid = new Cuboid(maxX + 1, maxY + 1, maxZ + 1, minX, minY, minZ);
        Selection selection = new Selection(cuboid, cuboid);
        Clipboard clipboard = new Clipboard();
        WorldEdits.copy(world, selection, new Vector3d(minX, minY, minZ), clipboard);
        clipboard.lock();
        for(int y = Math.min(0, countY); y <= Math.max(0, countY); ++y){
            for(int x = Math.min(0, countX); x <= Math.max(0, countX); ++x){
                for(int z = Math.min(0, countZ); z <= Math.max(0, countZ); ++z){
                    int offsetX = minX + x * (maxX - minX + 1);
                    int offsetY = minY + y * (maxY - minY + 1);
                    int offsetZ = minZ + z * (maxZ - minZ + 1);
                    paste(clipboard, world, new Vector3d(offsetX, offsetY, offsetZ));
                }
            }
        }
    }

    private static class BlockTransformApproximator {
        private static int[] approximate(AffineTransformation3d trans){
            trans = trans.linear();
            Vector3d transI = trans.apply(Vector3d.PLUS_I);
            Vector3d transJ = trans.apply(Vector3d.PLUS_J);
            Vector3d transK = trans.apply(Vector3d.PLUS_K);
            Set<Vector3d> candidateSet0 = Set.of(Vector3d.PLUS_I, Vector3d.MINUS_I,
                    Vector3d.PLUS_J, Vector3d.MINUS_J,
                    Vector3d.PLUS_K, Vector3d.MINUS_K);
            Set<Vector3d> candidateSet = new HashSet<>(candidateSet0);
            Vector3d nk = getNearestVector(transK, candidateSet.toArray(new Vector3d[0]));
            candidateSet.remove(nk);
            candidateSet.remove(nk.scalarMultiply(-1));
            Vector3d ni = getNearestVector(transI, candidateSet.toArray(new Vector3d[0]));
            candidateSet.remove(ni);
            candidateSet.remove(ni.scalarMultiply(-1));
            Vector3d nj = getNearestVector(transJ, candidateSet.toArray(new Vector3d[0]));
            candidateSet.remove(nj);
            candidateSet.remove(nj.scalarMultiply(-1));
            return new int[]{(int)Math.round(ni.x()), (int)Math.round(nj.x()), (int)Math.round(nk.x()),
                    (int)Math.round(ni.y()), (int)Math.round(nj.y()), (int)Math.round(nk.y()),
                    (int)Math.round(ni.z()), (int)Math.round(nj.z()), (int)Math.round(nk.z())};
        }

        static BlockTransformation approximateToBlockTrans(AffineTransformation3d trans) {
            int[] e = approximate(trans);
            Matrix3x3i matrix = new Matrix3x3i(e[0], e[1], e[2], e[3], e[4], e[5], e[6], e[7], e[8]);
            return new BlockTransformation(matrix);
        }

        private static Vector3d getNearestVector(Vector3d v, Vector3d... candidates){
            if(candidates.length == 0)throw new IllegalArgumentException();
            Vector3d nearest = candidates[0];
            double dis = v.distance(candidates[0]);
            for(Vector3d c : candidates){
                double disVc = v.distance(c);
                if(disVc < dis){
                    nearest = c;
                    dis = disVc;
                }
            }
            return nearest;
        }

    }

}
