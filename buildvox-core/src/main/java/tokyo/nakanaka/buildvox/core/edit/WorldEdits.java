package tokyo.nakanaka.buildvox.core.edit;

import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildVoxCore.blockSpace.*;
import tokyo.nakanaka.buildvox.core.blockSpace.BlockStateTransformingBlockSpace3;
import tokyo.nakanaka.buildvox.core.blockSpace.ClipboardBlockSpace3;
import tokyo.nakanaka.buildvox.core.blockSpace.IntegrityAdjustableBlockSpace3;
import tokyo.nakanaka.buildvox.core.blockStateTransformer.BlockTransformation;
import tokyo.nakanaka.buildvox.core.editWorld.EditWorld;
import tokyo.nakanaka.buildvox.core.math.region3d.Cuboid;
import tokyo.nakanaka.buildvox.core.math.transformation.AffineTransformation3d;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.selection.Selection;
import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;
import tokyo.nakanaka.buildvox.core.world.Block;
import tokyo.nakanaka.buildvox.core.world.World;
import tokyo.nakanaka.buildvox.core.blockSpace.BlockSpace3;

import java.util.Map;
import java.util.Set;

public class WorldEdits {
    private WorldEdits() {
    }

    /**
     * Copies the blocks in selection. A coordinate system is made which origin is the offset.
     * Each coordinate axis is divided by length 1, and the space is full by 1x1x1 cubes as a result. If the center of
     * the cube is contained in the selection, the cube is copy target. The copy block type is
     * the type of the center, and the block position in the clipboard is the same position as the made coordinate
     * system(Not original world coordinate system).
     */
    public static void copy(EditWorld editWorld, Selection selection, Vector3d offset, Clipboard clipboard) {
        var dest = new ClipboardBlockSpace3(clipboard);
        var trans = AffineTransformation3d.ofTranslation(-offset.x(), -offset.y(), -offset.z());
        BlockSpaceEdits.copy(editWorld, selection.calculateBlockPosSet(), dest, trans);
    }

    public static void copy(World srcWorld, Selection srcSel, Vector3d pos, Clipboard dest) {
        var destBs = new ClipboardBlockSpace3(dest);
        var trans = AffineTransformation3d.ofTranslation(-pos.x(), -pos.y(), -pos.z());
        BlockSpaceEdits.copy(new EditWorld(srcWorld), srcSel.calculateBlockPosSet(), destBs, trans);
    }

    public static void paste(Clipboard clipboard, EditWorld editWorld, Vector3d offset) {
        paste(clipboard, editWorld, offset, AffineTransformation3d.IDENTITY);
    }

    public static void paste(Clipboard clipboard, EditWorld editWorld, Vector3d offset, AffineTransformation3d clipboardTrans) {
        paste(clipboard, editWorld, offset, clipboardTrans, 1);
    }

    public static void paste(Clipboard clipboard, EditWorld editWorld, Vector3d offset, AffineTransformation3d clipboardTrans, double integrity) {
        BlockSpace3<Block> src = new ClipboardBlockSpace3(clipboard);
        Set<Vector3i> srcPosSet = clipboard.blockPosSet();
        BlockTransformation blockTrans = BlockTransformApproximator.approximateToBlockTrans(clipboardTrans);
        BlockSpace3<Block> dest = new BlockStateTransformingBlockSpace3(editWorld, BuildVoxSystem.environment.blockStateTransformer(), blockTrans);
        dest = new IntegrityAdjustableBlockSpace3<>(dest, integrity);
        AffineTransformation3d trans = AffineTransformation3d.ofTranslation(offset.x(), offset.y(), offset.z()).compose(clipboardTrans);
        BlockSpaceEdits.copy(src, srcPosSet, dest, trans);
    }

    public static void fill(EditWorld editWorld, Selection selection, Block block, double integrity) {
        BlockSpace3<Block> dest = new IntegrityAdjustableBlockSpace3<>(editWorld, integrity);
        BlockSpaceEdits.fill(dest, selection.calculateBlockPosSet(), block);
    }

    public static void replace(EditWorld editWorld, Selection selection, Block fromBlock, Block toBlock, double integrity) {
        BlockSpace3<Block> space = new IntegrityAdjustableBlockSpace3<>(editWorld, integrity);
        NamespacedId fromId = fromBlock.getId();
        Map<String, String> fromStateMap = fromBlock.getStateMap();
        BlockSpaceEdits.replace(space, selection.calculateBlockPosSet(),
            (BlockSpaceEdits.BlockCondition<Block>) (block) -> {
                NamespacedId id = block.getId();
                Map<String, String> stateMap = block.getStateMap();
                if(!fromId.equals(id)) return false;
                for(Map.Entry<String, String> entry : fromStateMap.entrySet()) {
                    String value = stateMap.get(entry.getKey());
                    if(!value.equals(entry.getValue())) return false;
                }
                return true;
            },
            toBlock);
    }

    public static void repeat(EditWorld editWorld, Vector3i pos0, Vector3i pos1,
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
        WorldEdits.copy(editWorld, selection, new Vector3d(minX, minY, minZ), clipboard);
        clipboard.lock();
        for(int y = Math.min(0, countY); y <= Math.max(0, countY); ++y){
            for(int x = Math.min(0, countX); x <= Math.max(0, countX); ++x){
                for(int z = Math.min(0, countZ); z <= Math.max(0, countZ); ++z){
                    int offsetX = minX + x * (maxX - minX + 1);
                    int offsetY = minY + y * (maxY - minY + 1);
                    int offsetZ = minZ + z * (maxZ - minZ + 1);
                    paste(clipboard, editWorld, new Vector3d(offsetX, offsetY, offsetZ));
                }
            }
        }
    }

}
