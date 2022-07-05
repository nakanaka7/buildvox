package tokyo.nakanaka.buildvox.core.edit;

import tokyo.nakanaka.buildvox.core.Axis;
import tokyo.nakanaka.buildvox.core.Clipboard;
import tokyo.nakanaka.buildvox.core.EditExit;
import tokyo.nakanaka.buildvox.core.block.VoxelBlock;
import tokyo.nakanaka.buildvox.core.clientWorld.ClientWorld;
import tokyo.nakanaka.buildvox.core.clientWorld.IntegrityClientWorld;
import tokyo.nakanaka.buildvox.core.clientWorld.PlayerClientWorld;
import tokyo.nakanaka.buildvox.core.math.Drawings;
import tokyo.nakanaka.buildvox.core.math.region3d.Parallelepiped;
import tokyo.nakanaka.buildvox.core.math.transformation.AffineTransformation3d;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.player.Player;
import tokyo.nakanaka.buildvox.core.selection.BlockSelection;
import tokyo.nakanaka.buildvox.core.selection.FillSelection;
import tokyo.nakanaka.buildvox.core.selection.PasteSelection;
import tokyo.nakanaka.buildvox.core.selection.Selection;
import tokyo.nakanaka.buildvox.core.selectionShape.PosArrayLengthException;
import tokyo.nakanaka.buildvox.core.selectionShape.SelectionShape;
import tokyo.nakanaka.buildvox.core.selectionShape.util.SelectionCreations;

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
     * General options.
     */
    public static class Options {
        /** masked */
        public boolean masked = false;
        /** integrity */
        public double integrity = 1.0;
        /** shape */
        public SelectionShape shape = null;
    }

    /**
     * Threw when create a selection from pos-array in createPosArraySelection().
     */
    public static class MissingPosException extends RuntimeException {
    }

    /**
     * Creates a selection from the pos-array. If shape is null, returns default selection.
     * @param posArray the pos-array. All the pos must not be null.
     * @param shape the shape. Nullable.
     * @return a selection from the pos-array.
     * @throws MissingPosException if some pos is null.
     */
    private static Selection createPosArraySelection(Vector3i[] posArray, SelectionShape shape) {
        boolean posArrayIsFull = Arrays.stream(posArray).allMatch(Objects::nonNull);
        if(!posArrayIsFull) throw new MissingPosException();
        if(shape == null) {
            return SelectionCreations.createDefault(posArray);
        }else {
            return shape.createSelection(posArray);
        }
    }

    /**
     * Creates a new non-block selection from pos-array, or reselect block-selection with the new options.
     * @param player the player.
     * @param options the options.
     */
    public static void select(Player player, Options options) {
        Selection sel = player.getSelection();
        if(sel == null) {
            sel = createPosArraySelection(player.getPosArrayClone(), options.shape);
        }
        PlayerClientWorld pcw = new PlayerClientWorld(player);
        if(sel instanceof BlockSelection blockSel) {
            blockSel.setBackwardBlocks(pcw);
            blockSel.setIntegrity(options.integrity);
            blockSel.setMasked(options.masked);
            blockSel.setForwardBlocks(pcw);
        }
        pcw.setSelection(sel);
        pcw.end();
    }

    /**
     * Applies physics in the selection.
     * @throws MissingPosException if player does not have a selection and some pos are missing.
     * @throws PosArrayLengthException if player does not have a selection and pos array length is not valid for
     */
    public static void applyPhysics(Player player, Options options) {
        Selection sel = player.getSelection();
        if(sel == null) {
            sel = createPosArraySelection(player.getPosArrayClone(), options.shape);
        }
        Clipboard clipboard = new Clipboard();
        ClientWorld clientWorld = new ClientWorld(player.getEditWorld(), true);
        WorldEdits.copy(clientWorld, sel, Vector3d.ZERO, clipboard);
        WorldEdits.fill(clientWorld, sel, VoxelBlock.valueOf("air"));
        WorldEdits.paste(clipboard, clientWorld, Vector3d.ZERO);
        Clipboard clipboard1 = new Clipboard();
        WorldEdits.copy(player.getEditWorld(), sel, Vector3d.ZERO, clipboard1);
        Selection pasteSel = new PasteSelection.Builder(clipboard1, Vector3d.ZERO, sel).build();
        player.setSelection(pasteSel);
    }

    /**
     * Reflects the blocks in the player's selection.
     * @param player the player.
     * @param axis the direction of reflection.
     * @param pos the block position which the reflection plane goes throw.
     * @return the edit exit.
     * @throws MissingPosException if player does not have a selection and some pos are missing.
     * @throws PosArrayLengthException if player does not have a selection and pos array length is not valid for
     * the shape.
     */
    public static EditExit reflect(Player player, Axis axis, Vector3d pos, Options options) {
        AffineTransformation3d relativeTrans = switch (axis){
            case X -> AffineTransformation3d.ofScale(- 1, 1, 1);
            case Y -> AffineTransformation3d.ofScale(1, - 1, 1);
            case Z -> AffineTransformation3d.ofScale(1, 1, - 1);
        };
        return affineTransform(player, pos, relativeTrans, options);
    }

    /**
     * Rotates the blocks in the player's selection.
     * @param player the player
     * @param axis the axis which parallels to the rotating-axis.
     * @param angle the rotation angle.
     * @param pos the block position which the rotating-axis goes throw.
     * @return the edit exit.
     * @throws MissingPosException if player does not have a selection and some pos are missing.
     * @throws PosArrayLengthException if player does not have a selection and pos array length is not valid for
     * the shape.
     */
    public static EditExit rotate(Player player, Axis axis, double angle, Vector3d pos, Options options) {
        double angleRad = angle * Math.PI / 180;
        AffineTransformation3d relativeTrans = switch (axis){
            case X -> AffineTransformation3d.ofRotationX(angleRad);
            case Y -> AffineTransformation3d.ofRotationY(angleRad);
            case Z -> AffineTransformation3d.ofRotationZ(angleRad);
        };
        return affineTransform(player, pos, relativeTrans, options);
    }

    /**
     * Scales the blocks in the player's selection.
     * @param player the player
     * @param factorX the scale factor about x-axis.
     * @param factorY the scale factor about y-axis.
     * @param factorZ the scale factor about z-axis.
     * @param pos the center position of scaling.
     * @return the edit exit.
     * @throws MissingPosException if player does not have a selection and some pos are missing.
     * @throws PosArrayLengthException if player does not have a selection and pos array length is not valid for
     * the shape.
     */
    public static EditExit scale(Player player, double factorX, double factorY, double factorZ, Vector3d pos, Options options) {
        if(factorX * factorY * factorZ == 0) throw new IllegalArgumentException();
        AffineTransformation3d relativeTrans = AffineTransformation3d.ofScale(factorX, factorY, factorZ);
        return affineTransform(player, pos, relativeTrans, options);
    }

    /**
     * Shears the blocks in the player's selection.
     * @param player the player.
     * @param axis the axis.
     * @param factorI the 1st factor.
     * @param factorJ the 2nd factor.
     * @param pos the center position of shearing.
     * @return the edit-exit
     * @throws MissingPosException if player does not have a selection and some pos are missing.
     * @throws PosArrayLengthException if player does not have a selection and pos array length is not valid for
     * the shape.
     */
    public static EditExit shear(Player player, Axis axis, double factorI, double factorJ, Vector3d pos, Options options) {
        AffineTransformation3d relativeTrans = switch (axis) {
            case X -> AffineTransformation3d.ofShearX(factorI, factorJ);
            case Y -> AffineTransformation3d.ofShearY(factorI, factorJ);
            case Z -> AffineTransformation3d.ofShearZ(factorI, factorJ);
        };
        return affineTransform(player, pos, relativeTrans, options);
    }

    /**
     * Translates the blocks in the player's selection.
     * @param player the player.
     * @param dx the displacement along x-axis.
     * @param dy the displacement along y-axis.
     * @param dz the displacement along z-axis.
     * @return the edit exit.
     * @throws MissingPosException if player does not have a selection and some pos are missing.
     * @throws PosArrayLengthException if player does not have a selection and pos array length is not valid for
     * the shape.
     */
    public static EditExit translate(Player player, double dx, double dy, double dz, Options options) {
        AffineTransformation3d relativeTrans = AffineTransformation3d.ofTranslation(dx, dy, dz);
        return affineTransform(player, Vector3d.ZERO, relativeTrans, options);
    }

    /**
     * Affine transform the player's selection.
     * @param player the player.
     * @param pos the block position of the center of affine transformation
     * @param relativeTrans the affine transformation.
     * @return the edit exit.
     * @throws MissingPosException if player does not have a selection and some pos are missing.
     * @throws PosArrayLengthException if player does not have a selection and pos array length is not valid for
     * the shape.
     */
    private static EditExit affineTransform(Player player, Vector3d pos, AffineTransformation3d relativeTrans, Options options) {
        AffineTransformation3d trans = AffineTransformation3d.withOffset(relativeTrans, pos.x() + 0.5, pos.y() + 0.5, pos.z() + 0.5);
        Selection selFrom = player.getSelection();
        Selection selTo;
        PlayerClientWorld pcw = new PlayerClientWorld(player);
        if(selFrom != null) {
            if(selFrom instanceof BlockSelection blockSel) {
                blockSel.setBackwardBlocks(pcw);
            }
        }else {
            Selection posArraySel = createPosArraySelection(player.getPosArrayClone(), options.shape);
            Clipboard clipboard = new Clipboard();
            WorldEdits.copy(pcw, posArraySel, Vector3d.ZERO, clipboard);
            selFrom = new PasteSelection.Builder(clipboard, Vector3d.ZERO, posArraySel)
                    .masked(options.masked).integrity(options.integrity).build();
            WorldEdits.fill(pcw, posArraySel, player.getBackgroundBlock());
        }
        selTo = selFrom.affineTransform(trans);
        if(selTo instanceof BlockSelection blockSel) {
            blockSel.setForwardBlocks(pcw);
        }
        pcw.setSelection(selTo);
        return pcw.end();
    }

    /**
     * @throws MissingPosException if player does not have a selection and some pos are missing.
     * @throws PosArrayLengthException if player does not have a selection and pos array length is not valid for
     * the shape.
     */
    public static EditExit cut(Player player, Vector3d pos, Options options) {
        Selection sel = player.getSelection();
        if(sel == null) {
            sel = createPosArraySelection(player.getPosArrayClone(), options.shape);
        }
        Clipboard clipboard = new Clipboard();
        WorldEdits.copy(player.getEditWorld(), sel, pos, clipboard);
        player.setClipboard(clipboard);
        PlayerClientWorld pcw = new PlayerClientWorld(player);
        if(sel instanceof BlockSelection blockSel) {
            blockSel.setBackwardBlocks(pcw);
        }else {
            WorldEdits.fill(pcw, sel, player.getBackgroundBlock());
        }
        pcw.setSelection(null);
        return pcw.end();
    }

    /**
     * Copies the blocks in the selection.
     * @param pos the position which corresponds to the origin of the clipboard.
     * @throws MissingPosException if player does not have a selection and some pos are missing.
     * @throws PosArrayLengthException if player does not have a selection and pos array length is not valid for
     * the shape.
     */
    public static EditExit copy(Player player, Vector3d pos, Options options) {
        Selection sel = player.getSelection();
        if(sel == null) {
            sel = createPosArraySelection(player.getPosArrayClone(), options.shape);
        }
        Clipboard clipboard = new Clipboard();
        WorldEdits.copy(new ClientWorld(player.getEditWorld()), sel, pos, clipboard);
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
        PasteSelection pasteSelection = new PasteSelection.Builder(clipboard, pos)
                .integrity(integrity)
                .masked(masked).build();
        PlayerClientWorld pw = new PlayerClientWorld(player);
        pasteSelection.setForwardBlocks(pw);
        pw.setSelection(pasteSelection);
        return pw.end();
    }

    /**
     * Fills the block into the selection
     * @param player the player.
     * @param block the block
     * @param options the fill options.
     * @return the edit-exit.
     * @throws MissingPosException if player does not have a selection and some pos are missing.
     * @throws PosArrayLengthException if player does not have a selection and pos array length is not valid for
     * the shape.
     */
    public static EditExit fill(Player player, VoxelBlock block, Options options) {
        Selection sel = player.getSelection();
        if(sel == null) {
            sel = createPosArraySelection(player.getPosArrayClone(), options.shape);
        }
        FillSelection fillSelection = new FillSelection.Builder(block, sel)
                .integrity(options.integrity)
                .masked(options.masked)
                .build();
        PlayerClientWorld pcw = new PlayerClientWorld(player);
        fillSelection.setForwardBlocks(pcw);
        pcw.setSelection(fillSelection);
        return pcw.end();
    }

    /**
     * Replaces blocks.
     * @param player the player.
     * @param blockFrom the block to be replaced from
     * @param blockTo the block to be replaced to
     * @param options the options.
     * @return the edit-exit.
     * @throws MissingPosException if player does not have a selection and some pos are missing.
     * @throws PosArrayLengthException if player does not have a selection and pos array length is not valid for
     * @throws IllegalArgumentException if integrity is less than 0 or larger than 1.
     */
    public static EditExit replace(Player player, VoxelBlock blockFrom, VoxelBlock blockTo, Options options) {
        Selection sel = player.getSelection();
        if(sel == null) {
            sel = createPosArraySelection(player.getPosArrayClone(), options.shape);
        }
        PlayerClientWorld pcw = new PlayerClientWorld(player);
        IntegrityClientWorld icw = new IntegrityClientWorld(options.integrity, pcw);
        WorldEdits.replace(icw, sel, blockFrom, blockTo);
        Clipboard clipboard = new Clipboard();
        WorldEdits.copy(pcw, sel, Vector3d.ZERO, clipboard);
        Selection pasteSel = new PasteSelection.Builder(clipboard, Vector3d.ZERO, sel).build();
        pcw.setSelection(pasteSel);
        return pcw.end();
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
        PlayerClientWorld pw = new PlayerClientWorld(player);
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
     * @throws PosArrayLengthException if player does not have a selection and pos array length is not valid for
     * @throws IllegalArgumentException if integrity is less than 0 or larger than 1.
     */
    public static EditExit repeat(Player player, int countX, int countY, int countZ, Options options) {
        Selection sel = player.getSelection();
        if(sel == null) {
            sel = createPosArraySelection(player.getPosArrayClone(), options.shape);
        }
        Parallelepiped bound = sel.getBound();
        double dx = bound.maxX() - bound.minX();
        double dy = bound.maxY() - bound.minY();
        double dz = bound.maxZ() - bound.minZ();
        Clipboard clip = new Clipboard();
        WorldEdits.copy(player.getEditWorld(), sel, Vector3d.ZERO, clip);
        List<Vector3i> positions = Drawings.line(Vector3i.ZERO, new Vector3i(countX, countY, countZ));
        PlayerClientWorld pw = new PlayerClientWorld(player);
        for(Vector3i pos : positions) {
            double qx = pos.x() * dx;
            double qy = pos.y() * dy;
            double qz = pos.z() * dz;
            Vector3d q = new Vector3d(qx, qy, qz);
            PasteSelection pasteSel = new PasteSelection.Builder(clip, q, sel.translate(q)).build();
            pasteSel.setForwardBlocks(pw);
            pw.setSelection(pasteSel);
        }
        return pw.end();
    }

}
