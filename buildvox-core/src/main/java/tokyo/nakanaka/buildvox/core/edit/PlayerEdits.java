package tokyo.nakanaka.buildvox.core.edit;

import tokyo.nakanaka.buildvox.core.*;
import tokyo.nakanaka.buildvox.core.block.VoxelBlock;
import tokyo.nakanaka.buildvox.core.clientWorld.ClientWorld;
import tokyo.nakanaka.buildvox.core.clientWorld.IntegrityClientWorld;
import tokyo.nakanaka.buildvox.core.clientWorld.PlayerClientWorld;
import tokyo.nakanaka.buildvox.core.BlockSettingOptions;
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
     * @throws PosArrayLengthException if the pos-array length is invalid for the shape.
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
     * Creates a new selection from existing selection or pos-array. If the new selection is block-selection, options
     * will be rebound.
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

    /*
     * Experimental
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
     * Reflects the blocks in the player's selection.
     * @param player the player.
     * @param axis the direction of reflection.
     * @param pos the block position which the reflection plane goes throw.
     * @return the edit exit.
     * @param shape the selection shape which is used when creating a new selection from pos-array.
     * @param blockSettingOptions the block-setting options.
     * @throws MissingPosException if player does not have a selection and some pos are missing.
     * @throws PosArrayLengthException if player does not have a selection and pos array length is not valid for
     * the shape.
     */
    public static EditExit reflect(Player player, Axis axis, Vector3d pos, SelectionShape shape, BlockSettingOptions blockSettingOptions) {
        AffineTransformation3d relativeTrans = switch (axis){
            case X -> AffineTransformation3d.ofScale(- 1, 1, 1);
            case Y -> AffineTransformation3d.ofScale(1, - 1, 1);
            case Z -> AffineTransformation3d.ofScale(1, 1, - 1);
        };
        return affineTransform(player, pos, relativeTrans, shape, blockSettingOptions);
    }

    /**
     * Rotates the blocks in the player's selection.
     * @param player the player
     * @param axis the axis which parallels to the rotating-axis.
     * @param angle the rotation angle.
     * @param pos the block position which the rotating-axis goes throw.
     * @param shape the selection shape which is used when creating a new selection from pos-array.
     * @param blockSettingOptions the block-setting options.
     * @return the edit exit.
     * @throws MissingPosException if player does not have a selection and some pos are missing.
     * @throws PosArrayLengthException if player does not have a selection and pos array length is not valid for
     * the shape.
     */
    public static EditExit rotate(Player player, Axis axis, double angle, Vector3d pos, SelectionShape shape, BlockSettingOptions blockSettingOptions) {
        double angleRad = angle * Math.PI / 180;
        AffineTransformation3d relativeTrans = switch (axis){
            case X -> AffineTransformation3d.ofRotationX(angleRad);
            case Y -> AffineTransformation3d.ofRotationY(angleRad);
            case Z -> AffineTransformation3d.ofRotationZ(angleRad);
        };
        return affineTransform(player, pos, relativeTrans, shape, blockSettingOptions);
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
     * Translates the blocks in the player's selection.
     * @param player the player.
     * @param dx the displacement along x-axis.
     * @param dy the displacement along y-axis.
     * @param dz the displacement along z-axis.
     * @param shape the selection shape which is used when creating a new selection from pos-array.
     * @param blockSettingOptions the block-setting options.
     * @return the edit exit.
     * @throws MissingPosException if player does not have a selection and some pos are missing.
     * @throws PosArrayLengthException if player does not have a selection and pos array length is not valid for
     * the shape.
     */
    public static EditExit translate(Player player, double dx, double dy, double dz, SelectionShape shape, BlockSettingOptions blockSettingOptions) {
        AffineTransformation3d relativeTrans = AffineTransformation3d.ofTranslation(dx, dy, dz);
        return affineTransform(player, Vector3d.ZERO, relativeTrans, shape, blockSettingOptions);
    }

    /**
     * Affine transform the selection. If player does not have a selection, a selection will be created from pos-array,
     * and then converted to a paste-selection. If the selection is block-selection, backward and forward blocks
     * will be set.
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
        if(selFrom == null) {
            Selection posArraySel = createPosArraySelection(player.getPosArrayClone(), options.shape);
            PasteSelection pasteSel = createPasteSelection(player.getEditWorld(), posArraySel);
            pasteSel.setIntegrity(options.integrity);
            pasteSel.setMasked(options.masked);
            selFrom = pasteSel;
        }
        PlayerClientWorld pcw = new PlayerClientWorld(player);
        if(selFrom instanceof BlockSelection blockSel) {
            blockSel.setBackwardBlocks(pcw);
        }
        Selection selTo;
        selTo = selFrom.affineTransform(trans);
        if(selTo instanceof BlockSelection blockSel) {
            blockSel.setForwardBlocks(pcw);
        }
        pcw.setSelection(selTo);
        return pcw.end();
    }

    /**
     * Affine transform the selection. If player does not have a selection, a selection will be created from pos-array,
     * and then converted to a paste-selection. If the selection is block-selection, backward and forward blocks
     * will be set.
     * @param player the player.
     * @param pos the block position of the center of affine transformation
     * @param shape the selection shape which is used when creating a new selection from pos-array.
     * @param blockSettingOptions the block-setting options.
     * @param relativeTrans the affine transformation.
     * @return the edit exit.
     * @throws MissingPosException if player does not have a selection and some pos are missing.
     * @throws PosArrayLengthException if player does not have a selection and pos array length is not valid for
     * the shape.
     */
    private static EditExit affineTransform(Player player, Vector3d pos, AffineTransformation3d relativeTrans,
                                            SelectionShape shape, BlockSettingOptions blockSettingOptions) {
        AffineTransformation3d trans = AffineTransformation3d.withOffset(relativeTrans, pos.x() + 0.5, pos.y() + 0.5, pos.z() + 0.5);
        Selection selFrom = player.getSelection();
        if(selFrom == null) {
            Selection posArraySel = createPosArraySelection(player.getPosArrayClone(), shape);
            PasteSelection pasteSel = createPasteSelection(player.getEditWorld(), posArraySel);
            pasteSel.setOptions(blockSettingOptions);
            selFrom = pasteSel;
        }
        PlayerClientWorld pcw = new PlayerClientWorld(player);
        if(selFrom instanceof BlockSelection blockSel) {
            blockSel.setBackwardBlocks(pcw);
        }
        Selection selTo;
        selTo = selFrom.affineTransform(trans);
        if(selTo instanceof BlockSelection blockSel) {
            blockSel.setForwardBlocks(pcw);
        }
        pcw.setSelection(selTo);
        return pcw.end();
    }

    private static PasteSelection createPasteSelection(World world, Selection sel) {
        Clipboard clipboard = new Clipboard(sel);
        WorldEdits.copy(world, sel, Vector3d.ZERO, clipboard);
        return new PasteSelection(clipboard, Vector3d.ZERO);
    }

    /**
     * Cuts the blocks in the selection. If player does not have a selection, a selection will be created from pos-array.
     * If the selection is block-selection, backward blocks will be set. If the selection is non-block selection, the
     * background blocks will be set. The selection of the player will be set null in the end.
     * @throws MissingPosException if player does not have a selection and some pos are missing.
     * @throws PosArrayLengthException if player does not have a selection and pos array length is not valid for
     * the shape.
     */
    public static EditExit cut(Player player, Vector3d pos, Options options) {
        Selection sel = player.getSelection();
        if(sel == null) {
            sel = createPosArraySelection(player.getPosArrayClone(), options.shape);
        }
        Clipboard clipboard = new Clipboard(sel.translate(pos.negate()));
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
     * Copies the blocks in the selection. If player does not have a selection, a selection will be created from pos-array.
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
        Clipboard clipboard = new Clipboard(sel.translate(pos.negate()));
        WorldEdits.copy(new ClientWorld(player.getEditWorld()), sel, pos, clipboard);
        clipboard.lock();
        player.setClipboard(clipboard);
        return new EditExit(clipboard.blockCount(), 0, 0);
    }

    /**
     * Pastes the blocks of the clipboard. A paste-selection will be set in the end.
     * @param player the player.
     * @param pos the position which corresponds to the origin of the clipboard.
     * @throws IllegalStateException if clipboard is null
     * @throws IllegalArgumentException if integrity is less than 0 or more than 1.
     * @deprecated Use paste() with blockSettingOptions parameters.
     */
    public static EditExit paste(Player player, Vector3d pos, double integrity, boolean masked) {
        Clipboard clipboard = player.getClipboard();
        if(clipboard == null){
            throw new IllegalStateException();
        }
        PasteSelection pasteSelection = new PasteSelection.Builder(clipboard, pos, clipboard.getSelection().translate(pos))
                .integrity(integrity)
                .masked(masked).build();
        PlayerClientWorld pw = new PlayerClientWorld(player);
        pasteSelection.setForwardBlocks(pw);
        pw.setSelection(pasteSelection);
        return pw.end();
    }

    /**
     * Pastes the blocks of the clipboard. A paste-selection will be set in the end.
     * @param player the player.
     * @param pos the position which corresponds to the origin of the clipboard.
     * @param blockSettingOptions the block-setting options.
     * @throws IllegalStateException if clipboard is null
     * @throws IllegalArgumentException if integrity is less than 0 or more than 1.
     */
    public static EditExit paste(Player player, Vector3d pos, BlockSettingOptions blockSettingOptions) {
        Clipboard clipboard = player.getClipboard();
        if(clipboard == null){
            throw new IllegalStateException();
        }
        PasteSelection pasteSel = new PasteSelection(clipboard, pos);
        pasteSel.setOptions(blockSettingOptions);
        PlayerClientWorld pw = new PlayerClientWorld(player);
        pasteSel.setForwardBlocks(pw);
        pw.setSelection(pasteSel);
        return pw.end();
    }

    /**
     * Fills the blocks into the selection. If player does not have a selection, a selection will be created from pos-array.
     * A fill-selection will be set in the end.
     * @param player the player.
     * @param block the block
     * @param options the fill options.
     * @return the edit-exit.
     * @throws MissingPosException if player does not have a selection and some pos are missing.
     * @throws PosArrayLengthException if player does not have a selection and pos array length is not valid for
     * the shape.
     * @deprecated Use fill() with shape and blockSettingOptions parameters.
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
     * Fills the blocks into the selection. If player does not have a selection, a selection will be created from pos-array.
     * A fill-selection will be set in the end.
     * @param player the player.
     * @param block the block
     * @param shape the selection shape which is used when creating a new selection from pos-array.
     * @param blockSettingOptions the block-setting options.
     * @return the edit-exit.
     * @throws MissingPosException if player does not have a selection and some pos are missing.
     * @throws PosArrayLengthException if player does not have a selection and pos array length is not valid for
     * the shape.
     */
    public static EditExit fill(Player player, VoxelBlock block, SelectionShape shape, BlockSettingOptions blockSettingOptions) {
        Selection sel = player.getSelection();
        if(sel == null) {
            sel = createPosArraySelection(player.getPosArrayClone(), shape);
        }
        FillSelection fillSelection = new FillSelection(sel, block);
        fillSelection.setOptions(blockSettingOptions);
        PlayerClientWorld pcw = new PlayerClientWorld(player);
        fillSelection.setForwardBlocks(pcw);
        pcw.setSelection(fillSelection);
        return pcw.end();
    }

    /**
     * Replaces blocks. If player does not have a selection, a selection will be created from pos-array. A paste-selection
     * will be set in the end.
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
        Selection pasteSel = createPasteSelection(player.getEditWorld(), sel);
        pcw.setSelection(pasteSel);
        return pcw.end();
    }

    /**
     * Repeats the blocks in the player selection. countX, countY, and countZ defines the repeating direction vector.
     * The end selection will be the paste selection of the last repeating blocks.
     * @param player the player.
     * @param countX the count along x-axis.
     * @param countY the count along y-axis.
     * @param countZ the count along z-axis.
     * @throws PosArrayLengthException if player does not have a selection and pos array length is not valid for
     * @throws IllegalArgumentException if integrity is less than 0 or larger than 1.
     * @deprecated Use repeat() with shape and blockSettingOptions parameters.
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
            PasteSelection pasteSel = new PasteSelection
                    .Builder(clip, q, sel.translate(q))
                    .masked(options.masked)
                    .integrity(options.integrity)
                    .build();
            pasteSel.setForwardBlocks(pw);
            pw.setSelection(pasteSel);
        }
        return pw.end();
    }

    /**
     * Repeats the blocks in the player selection. countX, countY, and countZ defines the repeating direction vector.
     * The end selection will be the paste selection of the last repeating blocks.
     * @param player the player.
     * @param countX the count along x-axis.
     * @param countY the count along y-axis.
     * @param countZ the count along z-axis.
     * @param shape the selection shape which is used when creating a new selection from pos-array.
     * @param blockSettingOptions the block-setting options.
     * @throws PosArrayLengthException if player does not have a selection and pos array length is not valid for
     * @throws IllegalArgumentException if integrity is less than 0 or larger than 1.
     */
    public static EditExit repeat(Player player, int countX, int countY, int countZ, SelectionShape shape, BlockSettingOptions blockSettingOptions) {
        Selection sel = player.getSelection();
        if(sel == null) {
            sel = createPosArraySelection(player.getPosArrayClone(), shape);
        }
        Parallelepiped bound = sel.getBound();
        double dx = bound.maxX() - bound.minX();
        double dy = bound.maxY() - bound.minY();
        double dz = bound.maxZ() - bound.minZ();
        Clipboard clip = new Clipboard(sel);
        WorldEdits.copy(player.getEditWorld(), sel, Vector3d.ZERO, clip);
        List<Vector3i> positions = Drawings.line(Vector3i.ZERO, new Vector3i(countX, countY, countZ));
        PlayerClientWorld pw = new PlayerClientWorld(player);
        for(Vector3i pos : positions) {
            double qx = pos.x() * dx;
            double qy = pos.y() * dy;
            double qz = pos.z() * dz;
            Vector3d q = new Vector3d(qx, qy, qz);
            PasteSelection pasteSel = new PasteSelection(clip, q);
            pasteSel.setOptions(blockSettingOptions);
            pasteSel.setForwardBlocks(pw);
            pw.setSelection(pasteSel);
        }
        return pw.end();
    }

}
