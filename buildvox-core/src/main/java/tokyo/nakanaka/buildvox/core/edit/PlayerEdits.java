package tokyo.nakanaka.buildvox.core.edit;

import tokyo.nakanaka.buildvox.core.math.Drawings;
import tokyo.nakanaka.buildvox.core.math.region3d.Parallelepiped;
import tokyo.nakanaka.buildvox.core.math.transformation.AffineTransformation3d;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.player.Player;
import tokyo.nakanaka.buildvox.core.property.Axis;
import tokyo.nakanaka.buildvox.core.selection.*;
import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;
import tokyo.nakanaka.buildvox.core.world.VoxelBlock;

import javax.swing.undo.UndoManager;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * The utility class of player edits.
 */
public class PlayerEdits {
    private PlayerEdits() {
    }

    /**
     * Undo the player's edits.
     * @param player the player.
     * @param count the counts to try undoing.
     * @return actual undo count
     */
    public static int undo(Player player, int count) {
        int remains = count;
        UndoManager undoManager = player.getUndoManager();
        while(remains > 0){
            if(undoManager.canUndo()){
                undoManager.undo();
                --remains;
            }else{
                break;
            }
        }
        return count - remains;
    }

    /**
     * Redo the player's edits.
     * @param player the player.
     * @param count the counts to try redoing.
     * @return actual redo count
     */
    public static int redo(Player player, int count) {
        int remains = count;
        UndoManager undoManager = player.getUndoManager();
        while(remains > 0){
            if(undoManager.canRedo()){
                undoManager.redo();
                --remains;
            }else{
                break;
            }
        }
        return count - remains;
    }

    /**
     * The exception which represents a selection was not found.
     */
    public static class SelectionNotFoundException extends RuntimeException {
    }

    private static Selection findSelection(Player player) {
        Selection selection = player.getSelection();
        if(selection != null) return selection;
        Vector3i[] posArray = player.getPosArrayClone();
        boolean posArrayIsFull = Arrays.stream(posArray).allMatch(Objects::nonNull);
        if(posArrayIsFull) {
            return SelectionCreations.createDefault(player.getPosArrayClone());
        }else{
            throw new SelectionNotFoundException();
        }
    }

    /**
     * Applies physics in the selection.
     * @throws SelectionNotFoundException if a selection is not found
     */
    public static void applyPhysics(Player player) {
        Selection selFrom = findSelection(player);
        Clipboard clipboard = new Clipboard();
        ClientWorld clientWorld = new ClientWorld(player.getEditWorld(), true);
        WorldEdits.copy(clientWorld, selFrom, Vector3d.ZERO, clipboard);
        WorldEdits.fill(clientWorld, selFrom, BuildVoxSystem.parseBlock("air"), 1);
        WorldEdits.paste(clipboard, clientWorld, Vector3d.ZERO);
        Selection selTo;
        if(selFrom instanceof BlockSelection bs) {
            selTo = bs.toNonBlock();
        }else {
            selTo = selFrom;
        }
        player.setSelection(selTo);
    }

    /**
     * Reflects the blocks in the player's selection.
     * @param player the player.
     * @param axis the direction of reflection.
     * @param pos the block position which the reflection plane goes throw.
     * @return the edit exit.
     */
    public static EditExit reflect(Player player, Axis axis, Vector3d pos) {
        AffineTransformation3d relativeTrans = switch (axis){
            case X -> AffineTransformation3d.ofScale(- 1, 1, 1);
            case Y -> AffineTransformation3d.ofScale(1, - 1, 1);
            case Z -> AffineTransformation3d.ofScale(1, 1, - 1);
        };
        return affineTransform(player, pos, relativeTrans);
    }

    /**
     * Rotates the blocks in the player's selection.
     * @param player the player
     * @param axis the axis which parallels to the rotating-axis.
     * @param angle the rotation angle.
     * @param pos the block position which the rotating-axis goes throw.
     * @return the edit exit.
     * @throws SelectionNotFoundException if a selection is not found
     */
    public static EditExit rotate(Player player, Axis axis, double angle, Vector3d pos) {
        double angleRad = angle * Math.PI / 180;
        AffineTransformation3d relativeTrans = switch (axis){
            case X -> AffineTransformation3d.ofRotationX(angleRad);
            case Y -> AffineTransformation3d.ofRotationY(angleRad);
            case Z -> AffineTransformation3d.ofRotationZ(angleRad);
        };
        return affineTransform(player, pos, relativeTrans);
    }

    /**
     * Scales the blocks in the player's selection.
     * @param player the player
     * @param factorX the scale factor about x-axis.
     * @param factorY the scale factor about y-axis.
     * @param factorZ the scale factor about z-axis.
     * @param pos the center position of scaling.
     * @return the edit exit.
     * @throws SelectionNotFoundException if a selection is not found
     */
    public static EditExit scale(Player player, double factorX, double factorY, double factorZ, Vector3d pos) {
        if(factorX * factorY * factorZ == 0) throw new IllegalArgumentException();
        AffineTransformation3d relativeTrans = AffineTransformation3d.ofScale(factorX, factorY, factorZ);
        return affineTransform(player, pos, relativeTrans);
    }

    /**
     * Shears the blocks in the player's selection.
     * @param player the player.
     * @param axis the axis.
     * @param factorI the 1st factor.
     * @param factorJ the 2nd factor.
     * @param pos the center position of shearing.
     * @return the edit-exit
     * @throws SelectionNotFoundException if a selection is not found
     */
    public static EditExit shear(Player player, Axis axis, double factorI, double factorJ, Vector3d pos) {
        AffineTransformation3d relativeTrans = switch (axis) {
            case X -> AffineTransformation3d.ofShearX(factorI, factorJ);
            case Y -> AffineTransformation3d.ofShearY(factorI, factorJ);
            case Z -> AffineTransformation3d.ofShearZ(factorI, factorJ);
        };
        return affineTransform(player, pos, relativeTrans);
    }

    /**
     * Translates the blocks in the player's selection.
     * @param player the player.
     * @param dx the displacement along x-axis.
     * @param dy the displacement along y-axis.
     * @param dz the displacement along z-axis.
     * @return the edit exit.
     * @throws SelectionNotFoundException if a selection is not found
     */
    public static EditExit translate(Player player, double dx, double dy, double dz) {
        AffineTransformation3d relativeTrans = AffineTransformation3d.ofTranslation(dx, dy, dz);
        return affineTransform(player, Vector3d.ZERO, relativeTrans);
    }

    /**
     * Affine transform the player's selection.
     * @param player the player.
     * @param pos the block position of the center of affine transformation
     * @param relativeTrans the affine transformation.
     * @return the edit exit.
     * @throws SelectionNotFoundException if a selection is not found
     */
    private static EditExit affineTransform(Player player, Vector3d pos, AffineTransformation3d relativeTrans) {
        Selection selectionFrom = findSelection(player);
        AffineTransformation3d trans = AffineTransformation3d.withOffset(relativeTrans, pos.x() + 0.5, pos.y() + 0.5, pos.z() + 0.5);
        BlockSelection selectionTo;
        if(selectionFrom instanceof BlockSelection blockSelection) {
            selectionTo = blockSelection.affineTransform(trans);
        }else {
            Clipboard clipboard = new Clipboard();
            Vector3d copyOffset = Vector3d.ZERO;
            WorldEdits.copy(new ClientWorld(player.getEditWorld()), selectionFrom, copyOffset, clipboard);
            clipboard.lock();
            AffineTransformation3d newClipTrans = trans.linear();
            Vector3d pasteOffset = trans.apply(copyOffset);
            selectionTo = PasteSelection.newInstance(clipboard, pasteOffset, newClipTrans);
        }
        PlayerWorld pw = new PlayerWorld(player);
        if (selectionFrom instanceof BlockSelection blockSelection) {
            blockSelection.setBackwardBlocks(pw);
        } else {
            WorldEdits.fill(pw, selectionFrom, player.getBackgroundBlock(), 1);
        }
        selectionTo.setForwardBlocks(pw);
        pw.setSelection(selectionTo);
        return pw.end();
    }

    /**
     * @throws SelectionNotFoundException if a selection is not found
     */
    public static EditExit cut(Player player, Vector3d pos) {
        PlayerWorld pw = new PlayerWorld(player);
        Selection selection = findSelection(player);
        Clipboard clipboard = new Clipboard();
        WorldEdits.copy(pw, selection, pos, clipboard);
        if (selection instanceof BlockSelection blockSelection) {
            blockSelection.setBackwardBlocks(pw);
        } else {
            WorldEdits.fill(pw, selection, player.getBackgroundBlock(), 1);
        }
        player.setClipboard(clipboard);
        pw.setSelection(null);
        return pw.end();
    }

    /**
     * Copies the blocks in the selection.
     * @param pos the position which corresponds to the origin of the clipboard.
     * @throws SelectionNotFoundException if a selection is not found
     */
    public static EditExit copy(Player player, Vector3d pos) {
        Selection selection = findSelection(player);
        Clipboard clipboard = new Clipboard();
        WorldEdits.copy(new ClientWorld(player.getEditWorld()), selection, pos, clipboard);
        clipboard.lock();
        player.setClipboard(clipboard);
        return new EditExit(clipboard.blockCount(), 0, 0);
    }

    /**
     * Pastes the blocks of the clipboard.
     * @param player the player.
     * @param pos the position which corresponds to the origin of the clipboard.
     * @throws IllegalStateException if clipboard is null
     * @throws IllegalArgumentException if integrity is less than 0 or more than 1.
     */
    public static EditExit paste(Player player, Vector3d pos, double integrity, boolean masked) {
        Clipboard clipboard = player.getClipboard();
        if(clipboard == null){
            throw new IllegalStateException();
        }
        PasteSelection pasteSelection = PasteSelection.newInstance(clipboard, pos, AffineTransformation3d.IDENTITY,
                integrity, masked, player.getBackgroundBlock());
        PlayerWorld pw = new PlayerWorld(player);
        pasteSelection.setForwardBlocks(pw);
        pw.setSelection(pasteSelection);
        return pw.end();
    }

    /**
     * Fills the block into the selection
     * @param player the player.
     * @param selection the selection.
     * @param block the block
     * @param integrity the integrity of block-setting.
     * @return the edit-exit.
     */
    public static EditExit fill(Player player, Selection selection, VoxelBlock block, double integrity) {
        FillSelection fillSelection = new FillSelection(selection.getRegion3d(), selection.getBound(), block, integrity);
        PlayerWorld pw = new PlayerWorld(player);
        fillSelection.setForwardBlocks(pw);
        pw.setSelection(fillSelection);
        return pw.end();
    }

    /**
     * Replaces blocks.
     * @param player the player.
     * @param blockFrom the block to be replaced from
     * @param blockTo the block to be replaced to
     * @param integrity the integrity of replacing.
     * @return the edit-exit.
     * @throws IllegalArgumentException if integrity is less than 0 or larger than 1.
     */
    public static EditExit replace(Player player, Selection sel, VoxelBlock blockFrom, VoxelBlock blockTo, double integrity) {
        if(integrity < 0 || 1 < integrity) throw new IllegalArgumentException();
        Selection selTo;
        if(sel instanceof BlockSelection bs) {
            selTo = bs.toNonBlock();
        }else {
            selTo = sel;
        }
        PlayerWorld pw = new PlayerWorld(player);
        WorldEdits.replace(pw, sel, blockFrom, blockTo, integrity);
        pw.setSelection(selTo);
        return pw.end();
    }

    /**
     * Replaces blocks.
     * @param player the player.
     * @param blockFrom the block to be replaced from
     * @param blockTo the block to be replaced to
     * @param integrity the integrity of replacing.
     * @return the edit-exit.
     * @throws IllegalArgumentException if integrity is less than 0 or larger than 1.
     * @throws SelectionNotFoundException if a selection is not found
     */
    public static EditExit replace(Player player, VoxelBlock blockFrom, VoxelBlock blockTo, double integrity) {
        Selection sel = findSelection(player);
        return replace(player, sel, blockFrom, blockTo, integrity);
    }

    /*
     * Repeats the blocks in the cuboid specified by pos0 and pos1.
     * @param player the player.
     * @param pos0 the position of a corner.
     * @param pos1 the position of another corner.
     * @param countX the count along x-axis.
     * @param countY the count along y-axis.
     * @param countZ the count along z-axis.
     */
    public static EditExit repeatOld(Player player, Vector3i pos0, Vector3i pos1, int countX, int countY, int countZ) {
        int maxX = Math.max(pos0.x(), pos1.x());
        int maxY = Math.max(pos0.y(), pos1.y());
        int maxZ = Math.max(pos0.z(), pos1.z());
        int minX = Math.min(pos0.x(), pos1.x());
        int minY = Math.min(pos0.y(), pos1.y());
        int minZ = Math.min(pos0.z(), pos1.z());
        int pos0x = pos0.x();
        int pos0y = pos0.y();
        int pos0z = pos0.z();
        int pos1x = pos1.x();
        int pos1y = pos1.y();
        int pos1z = pos1.z();
        int dx = Math.abs(pos1x - pos0x) + 1;
        int dy = Math.abs(pos1y - pos0y) + 1;
        int dz = Math.abs(pos1z - pos0z) + 1;
        if (countX >= 0) {
            if (pos1x >= pos0x) {
                pos1x += countX * dx;
            } else {
                pos0x += countX * dx;
            }
        } else {
            if (pos0x <= pos1x) {
                pos0x += countX * dx;
            } else {
                pos1x += countX * dx;
            }
        }
        if (countY >= 0) {
            if (pos1y >= pos0y) {
                pos1y += countY * dy;
            } else {
                pos0y += countY * dy;
            }
        } else {
            if (pos0y <= pos1y) {
                pos0y += countY * dy;
            } else {
                pos1y += countY * dy;
            }
        }
        if (countZ >= 0) {
            if (pos1z >= pos0z) {
                pos1z += countZ * dz;
            } else {
                pos0z += countZ * dz;
            }
        } else {
            if (pos0z <= pos1z) {
                pos0z += countZ * dz;
            } else {
                pos1z += countZ * dz;
            }
        }
        Vector3i pos0a = new Vector3i(pos0x, pos0y, pos0z);
        Vector3i pos1a = new Vector3i(pos1x, pos1y, pos1z);
        PlayerWorld pw = new PlayerWorld(player);
        WorldEdits.repeatOld(pw, new Vector3i(maxX, maxY, maxZ), new Vector3i(minX, minY, minZ), countX, countY, countZ);
        pw.setPosArray(new Vector3i[]{pos0a, pos1a});
        return pw.end();
    }

    /**
     * Repeats the blocks in the player selection. countX, countY, and countZ defines the repeating direction vector.
     * The end selection will be the paste selection of the last repeating blocks
     * @param player the player.
     * @param countX the count along x-axis.
     * @param countY the count along y-axis.
     * @param countZ the count along z-axis.
     * @throws SelectionNotFoundException if a selection is not found
     */
    public static EditExit repeat(Player player, int countX, int countY, int countZ) {
        Selection sel = findSelection(player);
        Parallelepiped bound = sel.getBound();
        double dx = bound.maxX() - bound.minX();
        double dy = bound.maxY() - bound.minY();
        double dz = bound.maxZ() - bound.minZ();
        Clipboard clip = new Clipboard();
        WorldEdits.copy(player.getEditWorld(), sel, Vector3d.ZERO, clip);
        List<Vector3i> positions = Drawings.line(Vector3i.ZERO, new Vector3i(countX, countY, countZ));
        PlayerWorld pw = new PlayerWorld(player);
        for(Vector3i pos : positions) {
            double qx = pos.x() * dx;
            double qy = pos.y() * dy;
            double qz = pos.z() * dz;
            PasteSelection pasteSel = PasteSelection.newInstance(clip, new Vector3d(qx, qy, qz), AffineTransformation3d.IDENTITY);
            pasteSel.setForwardBlocks(pw);
            pw.setSelection(pasteSel);
        }
        return pw.end();
    }

}
