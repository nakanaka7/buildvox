package tokyo.nakanaka.buildvox.core.edit;

import tokyo.nakanaka.buildvox.core.command.EditExit;
import tokyo.nakanaka.buildvox.core.editWorld.EditWorld;
import tokyo.nakanaka.buildvox.core.editWorld.RecordingEditWorld;
import tokyo.nakanaka.buildvox.core.math.transformation.AffineTransformation3d;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.player.Player;
import tokyo.nakanaka.buildvox.core.property.Axis;
import tokyo.nakanaka.buildVoxCore.selection.*;
import tokyo.nakanaka.buildvox.core.selection.*;
import tokyo.nakanaka.buildvox.core.world.Block;
import tokyo.nakanaka.buildvox.core.world.World;

import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

public class PlayerEdits {
    private PlayerEdits() {
    }

    /**
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
     * @throws SelectionNotFoundException if a selection is not found
     */
    public static void applyPhysics(Player player) {
        Selection selFrom = findSelection(player);
        Clipboard clipboard = new Clipboard();
        EditWorld editWorld = new EditWorld(player.getWorld(), true);
        WorldEdits.copy(editWorld, selFrom, Vector3d.ZERO, clipboard);
        WorldEdits.fill(editWorld, selFrom, Block.valueOf("air"), 1);
        WorldEdits.paste(clipboard, editWorld, Vector3d.ZERO);
        Selection selTo;
        if(selFrom instanceof BlockSelection bs) {
            selTo = bs.toNonBlock();
        }else {
            selTo = selFrom;
        }
        player.setSelectionWithPosArrayCleared(player.getWorld(), selTo);
    }

    public static EditExit reflect(Player player, Axis axis, Vector3d pos) {
        AffineTransformation3d relativeTrans = switch (axis){
            case X -> AffineTransformation3d.ofScale(- 1, 1, 1);
            case Y -> AffineTransformation3d.ofScale(1, - 1, 1);
            case Z -> AffineTransformation3d.ofScale(1, 1, - 1);
        };
        return affineTransform(player, pos, relativeTrans);
    }

    /**
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
     * @throws IllegalArgumentException if more than 0 factors are zero.
     */
    public static EditExit scale(Player player, double factorX, double factorY, double factorZ, Vector3d pos) {
        if(factorX * factorY * factorZ == 0) throw new IllegalArgumentException();
        AffineTransformation3d relativeTrans = AffineTransformation3d.ofScale(factorX, factorY, factorZ);
        return affineTransform(player, pos, relativeTrans);
    }

    public static EditExit shear(Player player, Axis axis, double factorI, double factorJ, Vector3d pos) {
        AffineTransformation3d relativeTrans = switch (axis) {
            case X -> AffineTransformation3d.ofShearX(factorI, factorJ);
            case Y -> AffineTransformation3d.ofShearY(factorI, factorJ);
            case Z -> AffineTransformation3d.ofShearZ(factorI, factorJ);
        };
        return affineTransform(player, pos, relativeTrans);
    }

    public static EditExit translate(Player player, double dx, double dy, double dz) {
        AffineTransformation3d relativeTrans = AffineTransformation3d.ofTranslation(dx, dy, dz);
        return affineTransform(player, Vector3d.ZERO, relativeTrans);
    }

    /**
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
            WorldEdits.copy(new EditWorld(player.getWorld()), selectionFrom, copyOffset, clipboard);
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
     * @throws SelectionNotFoundException if a selection is not found
     */
    public static EditExit copy(Player player, Vector3d pos) {
        Selection selection = findSelection(player);
        Clipboard clipboard = new Clipboard();
        WorldEdits.copy(new EditWorld(player.getWorld()), selection, pos, clipboard);
        clipboard.lock();
        player.setClipboard(clipboard);
        return new EditExit(clipboard.blockCount(), 0, 0);
    }

    /**
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

    public static EditExit fill(Player player, Selection selection, Block block, double integrity) {
        FillSelection fillSelection = new FillSelection(selection.getRegion3d(), selection.getBound(), block, integrity);
        return recordingEdit(player, fillSelection::setForwardBlocks, fillSelection
        );
    }

    /**
     * @throws IllegalArgumentException if integrity is less than 0 or larger than 1.
     * @throws SelectionNotFoundException if a selection is not found
     */
    public static EditExit replace(Player player, Block blockFrom, Block blockTo, double integrity) {
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
        RecordingEditWorld recordingEditWorld = new RecordingEditWorld(player.getWorld());
        editor.edit(recordingEditWorld);
        CompoundEdit compoundEdit = new CompoundEdit();
        UndoableEdit blockEdit = createBlockEdit(recordingEditWorld);
        compoundEdit.addEdit(blockEdit);
        UndoableEdit posArrayOrSelectionEdit;
        if(endSelection == null) {
            posArrayOrSelectionEdit = createPosArrayEdit(player, endPosArray);
            player.setPosArrayWithSelectionNull(player.getWorld(), endPosArray);
        }else {
            boolean endPosArrayContainsOnlyNull = Arrays.stream(endPosArray).allMatch(Objects::isNull);
            if(!endPosArrayContainsOnlyNull) {
                throw new IllegalArgumentException();
            }
            posArrayOrSelectionEdit = createSelectionEdit(player, endSelection);
            player.setSelectionWithPosArrayCleared(player.getWorld(), endSelection);
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
                    Block block = undoClipboard.getBlock(pos.x(), pos.y(), pos.z());
                    editWorld.setBlock(pos, block);
                }
            },
            () -> {
                Set<Vector3i> blockPosSet = redoClipboard.blockPosSet();
                for(Vector3i pos : blockPosSet) {
                    Block block = redoClipboard.getBlock(pos.x(), pos.y(), pos.z());
                    editWorld.setBlock(pos, block);
                }
            }
        );
    }

    /* Creates an edit to set a new pos array into the player */
    private static UndoableEdit createPosArrayEdit(Player player, Vector3i[] posArray) {
        World world = player.getWorld();
        Vector3i[] initPosArray = player.getPosArrayClone();
        Selection initSelection = player.getSelection();
        return createEdit(
            () -> {
                if(initSelection == null) {
                    player.setPosArrayWithSelectionNull(world, initPosArray.clone());
                }else{
                    player.setSelectionWithPosArrayCleared(world, initSelection);
                }
            },
            () -> player.setPosArrayWithSelectionNull(world, posArray)
        );
    }

    /* Creates an edit to set a new selection into the player */
    private static UndoableEdit createSelectionEdit(Player player, Selection selection) {
        World world = player.getWorld();
        Vector3i[] initPosArray = player.getPosArrayClone();
        Selection initSelection = player.getSelection();
        return createEdit(
            () -> {
                if(initSelection == null) {
                    player.setPosArrayWithSelectionNull(world, initPosArray.clone());
                }else{
                    player.setSelectionWithPosArrayCleared(world, initSelection);
                }
            },
            () -> player.setSelectionWithPosArrayCleared(world, selection)
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
