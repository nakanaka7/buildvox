package tokyo.nakanaka.buildvox.core.edit;

import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.block.BlockTransformation;
import tokyo.nakanaka.buildvox.core.block.StateImpl;
import tokyo.nakanaka.buildvox.core.blockSpace.*;
import tokyo.nakanaka.buildvox.core.blockSpace.editWorld.EditWorld;
import tokyo.nakanaka.buildvox.core.math.Drawings;
import tokyo.nakanaka.buildvox.core.math.region3d.Cuboid;
import tokyo.nakanaka.buildvox.core.math.region3d.Parallelepiped;
import tokyo.nakanaka.buildvox.core.math.transformation.AffineTransformation3d;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.selection.Selection;
import tokyo.nakanaka.buildvox.core.world.VoxelBlock;
import tokyo.nakanaka.buildvox.core.world.World;

import java.util.List;
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
    public static void copy(EditWorld srcWorld, Selection srcSel, Vector3d pos, Clipboard dest) {
        var clipDest = new ClipboardBlockSpace3(dest);
        var trans = AffineTransformation3d.ofTranslation(-pos.x(), -pos.y(), -pos.z());
        BlockSpaceEdits.copy(srcWorld, srcSel.calculateBlockPosSet(), clipDest, trans);
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
        var destBs = new ClipboardBlockSpace3(dest);
        var trans = AffineTransformation3d.ofTranslation(-pos.x(), -pos.y(), -pos.z());
        BlockSpaceEdits.copy(new EditWorld(srcWorld), srcSel.calculateBlockPosSet(), destBs, trans);
    }

    /**
     * Pastes the clipboard contents into the destination world.
     * @param srcClip the source clipboard.
     * @param dest the destination world.
     * @param pos the position of the world which corresponds to the origin of the clipboard.
     */
    public static void paste(Clipboard srcClip, EditWorld dest, Vector3d pos) {
        paste(srcClip, dest, pos, AffineTransformation3d.IDENTITY);
    }

    /**
     * Pastes the clipboard contents into the destination world.
     * @param srcClip the source clipboard.
     * @param dest the destination world.
     * @param pos the position of the world which corresponds to the origin of the clipboard.
     * @param clipboardTrans the clipboard transformation
     */
    public static void paste(Clipboard srcClip, EditWorld dest, Vector3d pos, AffineTransformation3d clipboardTrans) {
        paste(srcClip, dest, pos, clipboardTrans, 1);
    }

    /**
     * Pastes the clipboard contents into the destination world.
     * @param srcClip the source clipboard.
     * @param dest the destination world.
     * @param pos the position of the world which corresponds to the origin of the clipboard.
     * @param clipboardTrans the clipboard transformation
     * @param integrity the integrity of the block-settings.
     */
    public static void paste(Clipboard srcClip, EditWorld dest, Vector3d pos, AffineTransformation3d clipboardTrans, double integrity) {
        BlockSpace3<VoxelBlock> src = new ClipboardBlockSpace3(srcClip);
        Set<Vector3i> srcPosSet = srcClip.blockPosSet();
        BlockTransformation blockTrans = BlockTransformApproximator.approximateToBlockTrans(clipboardTrans);
        BlockSpace3<VoxelBlock> transDest = new BlockStateTransformingBlockSpace3(dest, blockTrans);
        transDest = new IntegrityAdjustableBlockSpace3<>(transDest, integrity);
        AffineTransformation3d trans = AffineTransformation3d.ofTranslation(pos.x(), pos.y(), pos.z()).compose(clipboardTrans);
        BlockSpaceEdits.copy(src, srcPosSet, transDest, trans);
    }

    /**
     * Fill the block in the selection.
     * @param world the world.
     * @param sel the selection.
     * @param block the block.
     * @param integrity the integrity of block-setting.
     */
    public static void fill(EditWorld world, Selection sel, VoxelBlock block, double integrity) {
        BlockSpace3<VoxelBlock> dest = new IntegrityAdjustableBlockSpace3<>(world, integrity);
        BlockSpaceEdits.fill(dest, sel.calculateBlockPosSet(), block);
    }

    /**
     * Replaces the blocks in the selection.
     * @param world the world
     * @param sel the selection
     * @param fromBlock the block type to be replaced from.
     * @param toBlock the block type to be replaced to.
     * @param integrity the integrity of block-setting.
     */
    public static void replace(EditWorld world, Selection sel, VoxelBlock fromBlock, VoxelBlock toBlock, double integrity) {
        BlockSpace3<VoxelBlock> space = new IntegrityAdjustableBlockSpace3<>(world, integrity);
        NamespacedId fromId = fromBlock.getBlockId();
        Map<String, String> fromStateMap = ((StateImpl)fromBlock.getState()).getStateMap();
        BlockSpaceEdits.replace(space, sel.calculateBlockPosSet(),
            (BlockSpaceEdits.BlockCondition<VoxelBlock>) (block) -> {
                NamespacedId id = block.getBlockId();
                Map<String, String> stateMap = ((StateImpl)block.getState()).getStateMap();
                if(!fromId.equals(id)) return false;
                for(Map.Entry<String, String> entry : fromStateMap.entrySet()) {
                    String value = stateMap.get(entry.getKey());
                    if(!value.equals(entry.getValue())) return false;
                }
                return true;
            },
            toBlock);
    }

    /**
     * Repeats the blocks in the cuboid specified by pos0 and pos1.
     * @param world the world
     * @param pos0 the position of a corner.
     * @param pos1 the position of another corner.
     * @param countX the count along x-axis.
     * @param countY the count along y-axis.
     * @param countZ the count along z-axis.
     */
    public static void repeat(EditWorld world, Vector3i pos0, Vector3i pos1,
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
    /**
     * Repeats the blocks in the selection. countX, countY, and countZ defines the repeating direction vector.
     * @param world the world.
     * @param sel the selection.
     * @param countX the count along x-axis.
     * @param countY the count along y-axis.
     * @param countZ the count along z-axis.
     */
    public static void repeat(EditWorld world, Selection sel, int countX, int countY, int countZ) {
        Parallelepiped bound = sel.getBound();
        double maxX = bound.maxX();
        double maxY = bound.maxY();
        double maxZ = bound.maxZ();
        double minX = bound.minX();
        double minY = bound.minY();
        double minZ = bound.minZ();
        Clipboard clipboard = new Clipboard();
        WorldEdits.copy(world, sel, new Vector3d(minX, minY, minZ), clipboard);
        clipboard.lock();

        List<Vector3i> positions = Drawings.line(Vector3i.ZERO, new Vector3i(countX, countY, countZ));
        for(var pos : positions) {
            double posX = minX + pos.x() * (maxX - minX);
            double posY = minY + pos.y() * (maxY - minY);
            double posZ = minZ + pos.z() * (maxZ - minZ);
            paste(clipboard, world, new Vector3d(posX, posY, posZ));
        }
    }

}
