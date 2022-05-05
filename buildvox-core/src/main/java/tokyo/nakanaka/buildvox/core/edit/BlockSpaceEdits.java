package tokyo.nakanaka.buildvox.core.edit;

import tokyo.nakanaka.buildvox.core.blockSpace.BlockSpace3;
import tokyo.nakanaka.buildvox.core.math.region3d.Cuboid;
import tokyo.nakanaka.buildvox.core.math.transformation.AffineTransformation3d;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.selection.Selection;

import java.util.Set;

public class BlockSpaceEdits {
    private BlockSpaceEdits() {
    }

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

    public static <B> void fill(BlockSpace3<B> dest, Set<Vector3i> destPosSet, B block) {
        for(Vector3i e : destPosSet){
            dest.setBlock(e, block);
        }
    }

    public static <B> void replace(BlockSpace3<B> space, Set<Vector3i> posSet, B fromBlock, B toBlock) {
        for(Vector3i pos : posSet) {
            B a = space.getBlock(pos);
            if(a.equals(fromBlock)) {
                space.setBlock(pos, toBlock);
            }
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
