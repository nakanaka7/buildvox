package tokyo.nakanaka.buildvox.core.edit;

import tokyo.nakanaka.buildvox.core.math.region3d.Cuboid;
import tokyo.nakanaka.buildvox.core.math.transformation.AffineTransformation3d;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.selection.Selection;

import java.util.Set;
import java.util.function.Predicate;

/**
 * The utility class of block space edits.
 */
public class VoxelSpaceEdits {
    private VoxelSpaceEdits() {
    }

    /**
     * Copy the source blocks and paste them into the destination space.
     * @param src the source block space.
     * @param srcPosSet the pos set of the source.
     * @param dest the destination block space.
     * @param trans the affine transformation.
     * @param <B> the block type.
     */
    public static <B> void copy(VoxelSpace<B> src, Set<Vector3i> srcPosSet, VoxelSpace<B> dest, AffineTransformation3d trans) {
        for(Vector3i pos : srcPosSet) {
            B block = src.getBlock(pos);
            Vector3d posMin = pos.toVector3d();
            Vector3d posMax = posMin.add(1, 1, 1);
            Cuboid cuboid = new Cuboid(posMin, posMax);
            Selection cube = new Selection(cuboid, cuboid);
            Selection transCube = cube.affineTransform(trans);
            Set<Vector3i> blockPosSet = transCube.calculateBlockPosSet();
            for(Vector3i e : blockPosSet){
                dest.setBlock(e, block);
            }
        }
    }

    /**
     * Fill the blocks into the destination.
     * @param dest the destination block space.
     * @param destPosSet the destination pos set.
     * @param block the block.
     * @param <B> the block type.
     */
    public static <B> void fill(VoxelSpace<B> dest, Set<Vector3i> destPosSet, B block) {
        for(Vector3i e : destPosSet){
            dest.setBlock(e, block);
        }
    }

    /**
     * Replace the blocks of the space to the blocks of another type.
     * @param space the block space.
     * @param posSet the pos set of blocks.
     * @param blockCondition block-replacing condition.
     * @param toBlock the block type after replacing.
     * @param <B> the block type.
     */
    public static <B> void replace(VoxelSpace<B> space, Set<Vector3i> posSet, Predicate<B> blockCondition, B toBlock) {
        for(Vector3i pos : posSet) {
            B a = space.getBlock(pos);
            if(blockCondition.test(a)) {
                space.setBlock(pos, toBlock);
            }
        }
    }

}
