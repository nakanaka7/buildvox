package tokyo.nakanaka.buildvox.core.edit;

import tokyo.nakanaka.buildvox.core.BuildVoxSystem;
import tokyo.nakanaka.buildvox.core.blockSpace.Clipboard;
import tokyo.nakanaka.buildvox.core.blockSpace.editWorld.EditWorld;
import tokyo.nakanaka.buildvox.core.blockSpace.editWorld.RecordingEditWorld;
import tokyo.nakanaka.buildvox.core.command.EditExit;
import tokyo.nakanaka.buildvox.core.math.Drawings;
import tokyo.nakanaka.buildvox.core.math.region3d.Parallelepiped;
import tokyo.nakanaka.buildvox.core.math.transformation.AffineTransformation3d;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.player.Player;
import tokyo.nakanaka.buildvox.core.property.Axis;
import tokyo.nakanaka.buildvox.core.selection.*;
import tokyo.nakanaka.buildvox.core.world.VoxelBlock;
import tokyo.nakanaka.buildvox.core.world.World;

import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
        EditWorld editWorld = new EditWorld(player.getEditTargetWorld(), true);
        WorldEdits.copy(editWorld, selFrom, Vector3d.ZERO, clipboard);
        WorldEdits.fill(editWorld, selFrom, BuildVoxSystem.parseBlock("air"), 1);
        WorldEdits.paste(clipboard, editWorld, Vector3d.ZERO);
        Selection selTo;
        if(selFrom instanceof BlockSelection bs) {
            selTo = bs.toNonBlock();
        }else {
            selTo = selFrom;
        }
        player.setSelection(player.getEditTargetWorld(), selTo);
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
            WorldEdits.copy(new EditWorld(player.getEditTargetWorld()), selectionFrom, copyOffset, clipboard);
            clipboard.lock();
            AffineTransformation3d newClipTrans = trans.linear();
            Vector3d pasteOffset = trans.apply(copyOffset);
            selectionTo = PasteSelection.newInstance(clipboard, pasteOffset, newClipTrans);
        }
        return recordingEdit(player,
            (editWorld) -> {
                if (selectionFrom instanceof BlockSelection blockSelection) {
                    blockSelection.setBackwardBlocks(editWorld);
                } else {
                    WorldEdits.fill(editWorld, selectionFrom, player.getBackgroundBlock(), 1);
                }
                selectionTo.setForwardBlocks(editWorld);
            }, selectionTo
        );
    }

    /**
     * @throws SelectionNotFoundException if a selection is not found
     */
    public static EditExit cut(Player player, Vector3d pos) {
        return recordingEdit(player,
            (editWorld) -> {
                Selection selection = findSelection(player);
                Clipboard clipboard = new Clipboard();
                WorldEdits.copy(editWorld, selection, pos, clipboard);
                if (selection instanceof BlockSelection blockSelection) {
                    blockSelection.setBackwardBlocks(editWorld);
                } else {
                    WorldEdits.fill(editWorld, selection, player.getBackgroundBlock(), 1);
                }
                player.setClipboard(clipboard);
            }
        );
    }

    /**
     * Copies the blocks in the selection.
     * @param pos the position which corresponds to the origin of the clipboard.
     * @throws SelectionNotFoundException if a selection is not found
     */
    public static EditExit copy(Player player, Vector3d pos) {
        Selection selection = findSelection(player);
        Clipboard clipboard = new Clipboard();
        WorldEdits.copy(new EditWorld(player.getEditTargetWorld()), selection, pos, clipboard);
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
    public static EditExit paste(Player player, Vector3d pos, double integrity) {
        Clipboard clipboard = player.getClipboard();
        if(clipboard == null){
            throw new IllegalStateException();
        }
        PasteSelection pasteSelection = PasteSelection.newInstance(clipboard, pos, AffineTransformation3d.IDENTITY, integrity);
        return recordingEdit(player, pasteSelection::setForwardBlocks, pasteSelection);
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
        return recordingEdit(player, fillSelection::setForwardBlocks, fillSelection
        );
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
        if(integrity < 0 || 1 < integrity) throw new IllegalArgumentException();
        Selection selFrom = findSelection(player);
        Selection selTo;
        if(selFrom instanceof BlockSelection bs) {
            selTo = bs.toNonBlock();
        }else {
            selTo = selFrom;
        }
        return recordingEdit(
            player, (editWorld) -> WorldEdits.replace(editWorld, selFrom, blockFrom, blockTo, integrity), selTo
        );
    }

    /**
     * Repeats the blocks in the cuboid specified by pos0 and pos1.
     * @param player the player.
     * @param pos0 the position of a corner.
     * @param pos1 the position of another corner.
     * @param countX the count along x-axis.
     * @param countY the count along y-axis.
     * @param countZ the count along z-axis.
     */
    public static EditExit repeat(Player player, Vector3i pos0, Vector3i pos1, int countX, int countY, int countZ) {
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
        return recordingEdit(player,
                (editWorld) -> WorldEdits.repeat(editWorld, new Vector3i(maxX, maxY, maxZ), new Vector3i(minX, minY, minZ), countX, countY, countZ)
                , new Vector3i[]{pos0a, pos1a}
        );
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
        WorldEdits.copy(player.getWorld(), sel, Vector3d.ZERO, clip);
        List<Vector3i> positions = Drawings.line(Vector3i.ZERO, new Vector3i(countX, countY, countZ));
        PlayerWorld pw = PlayerWorld.newInstance(player);
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

    /**
     * An edit world for a player. Calling end() stores an undoable edit
     * into the player.
     */
    private static class PlayerWorld extends RecordingEditWorld {
        private Player player;
        private Selection sel;

        private PlayerWorld(World original, Player player) {
            super(original);
            this.player = player;
            this.sel = player.getSelection();
        }

        /**
         * Creates a new instance of the player.
         * @param player the player.
         * @return a new instance.
         */
        public static PlayerWorld newInstance(Player player) {
            var world = player.getEditTargetWorld();
            return new PlayerWorld(world, player);
        }

        /**
         * Set the selection.
         * @param sel the selection.
         */
        public void setSelection(Selection sel) {
            this.sel = sel;
        }

        /**
         * Stores the selection change and block changes as one edit into player.
         * @return the edit exit.
         */
        public EditExit end() {
            UndoableEdit selEdit = createSelectionEdit(player, sel);
            player.setSelection(sel);
            UndoableEdit blockEdit = createBlockEdit(this);
            CompoundEdit compoundEdit = new CompoundEdit();
            compoundEdit.addEdit(selEdit);
            compoundEdit.addEdit(blockEdit);
            compoundEdit.end();
            player.getUndoManager().addEdit(compoundEdit);
            return new EditExit(this.blockCount(), 0, 0);
        }

    }

    /**
     * A functional interface for recordingEdit() which defines editing for the world.
     */
    private interface EditWorldEditor {
        void edit(EditWorld editWorld);
    }

    /**
     * Recording edit end with all null pos array and null selection.
     */
    private static EditExit recordingEdit(Player player, EditWorldEditor editor) {
        return recordingEdit(player, editor, new Vector3i[player.getPosArrayClone().length], null);
    }

    /**
     * Recording edit end with pos array and null selection.
     */
    private static EditExit recordingEdit(Player player, EditWorldEditor editor, Vector3i[] endPosArray) {
        return recordingEdit(player, editor, endPosArray, null);
    }

    /**
     * Recording edit end with all null pos array and (nonNull assumed) selection.
     */
    private static EditExit recordingEdit(Player player, EditWorldEditor editor, Selection endSelection) {
        return recordingEdit(player, editor, new Vector3i[player.getPosArrayClone().length], endSelection);
    }

    /**
     * Edits world, sets a new pos array or selection into the player, and stores the undoable edit of the above procedures
     * as one edit into the player's UndoManager.
     * @param player a player.
     * @param editor an editor which defines the world editing.
     * @param endPosArray the pos array in the end.
     * @param endSelection the selection in the end.
     * @return the edit exit of the world editing.
     * @throws IllegalArgumentException if endPosArray contains nonNull and selection is not null.
     */
    private static EditExit recordingEdit(Player player, EditWorldEditor editor, Vector3i[] endPosArray, Selection endSelection) {
        RecordingEditWorld recordingEditWorld = new RecordingEditWorld(player.getEditTargetWorld());
        editor.edit(recordingEditWorld);
        CompoundEdit compoundEdit = new CompoundEdit();
        UndoableEdit blockEdit = createBlockEdit(recordingEditWorld);
        compoundEdit.addEdit(blockEdit);
        UndoableEdit posArrayOrSelectionEdit;
        if(endSelection == null) {
            posArrayOrSelectionEdit = createPosArrayEdit(player, endPosArray);
            player.setPosArray(player.getEditTargetWorld(), endPosArray);
        }else {
            boolean endPosArrayContainsOnlyNull = Arrays.stream(endPosArray).allMatch(Objects::isNull);
            if(!endPosArrayContainsOnlyNull) {
                throw new IllegalArgumentException();
            }
            posArrayOrSelectionEdit = createSelectionEdit(player, endSelection);
            player.setSelection(player.getEditTargetWorld(), endSelection);
        }
        compoundEdit.addEdit(posArrayOrSelectionEdit);
        compoundEdit.end();
        player.getUndoManager().addEdit(compoundEdit);
        return new EditExit(recordingEditWorld.blockCount(), 0, 0);
    }

    /* Creates an undoable edit for edit world that is target of recordingEditWorld */
    private static UndoableEdit createBlockEdit(RecordingEditWorld recordingEditWorld) {
        Clipboard undoClipboard = recordingEditWorld.getUndoClipboard();
        Clipboard redoClipboard = recordingEditWorld.getRedoClipboard();
        EditWorld editWorld = new EditWorld(recordingEditWorld.getOriginal());
        return createEdit(
            () -> {
                Set<Vector3i> blockPosSet = undoClipboard.blockPosSet();
                for(Vector3i pos : blockPosSet) {
                    VoxelBlock block = undoClipboard.getBlock(pos.x(), pos.y(), pos.z());
                    editWorld.setBlock(pos, block);
                }
            },
            () -> {
                Set<Vector3i> blockPosSet = redoClipboard.blockPosSet();
                for(Vector3i pos : blockPosSet) {
                    VoxelBlock block = redoClipboard.getBlock(pos.x(), pos.y(), pos.z());
                    editWorld.setBlock(pos, block);
                }
            }
        );
    }

    /* Creates an edit to set a new pos array into the player */
    private static UndoableEdit createPosArrayEdit(Player player, Vector3i[] posArray) {
        World world = player.getEditTargetWorld();
        Vector3i[] initPosArray = player.getPosArrayClone();
        Selection initSelection = player.getSelection();
        return createEdit(
            () -> {
                if(initSelection == null) {
                    player.setPosArray(world, initPosArray.clone());
                }else{
                    player.setSelection(world, initSelection);
                }
            },
            () -> player.setPosArray(world, posArray)
        );
    }

    /* Creates an edit to set a new selection into the player */
    private static UndoableEdit createSelectionEdit(Player player, Selection selection) {
        World world = player.getEditTargetWorld();
        Vector3i[] initPosArray = player.getPosArrayClone();
        Selection initSelection = player.getSelection();
        return createEdit(
            () -> {
                if(initSelection == null) {
                    player.setPosArray(world, initPosArray.clone());
                }else{
                    player.setSelection(world, initSelection);
                }
            },
            () -> player.setSelection(world, selection)
        );
    }

    /**
     * Creates an UndoableEdit from undoRunnable and redoRunnable.
     * @param undoRunnable a runnable for undo.
     * @param redoRunnable a runnable for redo.
     * @return an instance
     */
    private static UndoableEdit createEdit(Runnable undoRunnable, Runnable redoRunnable) {
        return new UndoableEdit() {
            @Override
            public void undo() {
                undoRunnable.run();
            }

            @Override
            public boolean canUndo() {
                return true;
            }

            @Override
            public void redo() {
                redoRunnable.run();
            }

            @Override
            public boolean canRedo() {
                return true;
            }

            @Override
            public void die() {

            }

            @Override
            public boolean addEdit(UndoableEdit anEdit) {
                return false;
            }

            @Override
            public boolean replaceEdit(UndoableEdit anEdit) {
                return false;
            }

            @Override
            public boolean isSignificant() {
                return true;
            }

            @Override
            public String getPresentationName() {
                return "";
            }

            @Override
            public String getUndoPresentationName() {
                return "";
            }

            @Override
            public String getRedoPresentationName() {
                return "";
            }
        };
    }

}
