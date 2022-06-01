package tokyo.nakanaka.buildvox.core.edit;

import tokyo.nakanaka.buildvox.core.blockSpace.BlockSpace3;
import tokyo.nakanaka.buildvox.core.math.region3d.Cuboid;
import tokyo.nakanaka.buildvox.core.math.transformation.AffineTransformation3d;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.selection.Selection;

import java.util.Set;

/**
 * The utility class of block space edits.
 */
public class BlockSpaceEdits {
    private BlockSpaceEdits() {
    }

    /**
     * Copy the source blocks and paste them into the destination space.
     * @param src the source block space.
     * @param srcPosSet the pos set of the source.
     * @param dest the destination block space.
     * @param trans the affine transformation.
     * @param <B> the block type.
     */
    public static <B> void copy(BlockSpace3<B> src, Set<Vector3i> srcPosSet, BlockSpace3<B> dest, AffineTransformation3d trans) {
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
    public static <B> void fill(BlockSpace3<B> dest, Set<Vector3i> destPosSet, B block) {
        for(Vector3i e : destPosSet){
            dest.setBlock(e, block);
        }
    }

    public static <B> void replace(BlockSpace3<B> space, Set<Vector3i> posSet, BlockCondition<B> condition, B toBlock) {
        for(Vector3i pos : posSet) {
            B a = space.getBlock(pos);
            if(condition.match(a)) {
                space.setBlock(pos, toBlock);
            }
        }
    }

    public static interface BlockCondition<B> {
        boolean match(B block);
    }

}
