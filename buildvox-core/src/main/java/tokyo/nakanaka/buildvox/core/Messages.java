package tokyo.nakanaka.buildvox.core;

import java.util.List;

public class Messages {
    public static final String NOT_FOUND_DUMMY_PLAYER_ERROR = "Cannot find the dummy player.";
    public static final String SESSION_NULL_ERROR = "Player must be specified.";
    public static final String POS_ARRAY_LENGTH_ERROR = "Pos array length must be 2..4.";
    public static final String POS_DATA_ERROR = "Have illegal pos";
    public static final String CANNOT_FIND_PLAYER_ERROR = "Cannot find a player.";
    public static final String UNDO_REDO_COUNT_ERROR = "The count must be larger than 0.";
    public static final String MISSING_POS_ERROR = "Some pos data is missing.";
    public static final String SELECTION_NULL_ERROR = "Selection is empty.";
    public static final String SCALE_FACTOR_ERROR = "The scale factor must not be 0.";
    public static final String INTEGRITY_ERROR = "The integrity must be between 0 and 1 (inclusive).";

    public static String ofNotFoundWorldError(String worldName){
        return "Cannot find the world \"" + worldName + "\"";
    }

    public static String ofDummyPlayerAlreadyExistError(String playerName){
        return "A dummy player \"" + playerName + "\"already exists.";
    }

    public static String ofAddNewDummyPlayerExit(String playerName){
        return "Add a new dummy player \"" + playerName + "\"";
    }

    public static String ofNotFoundDummyPlayerError(String playerName){
        return "A dummy player \"" + playerName + "\" is not found.";
    }

    public static String ofRemoveDummyPlayerExit(String playerName){
        return "Remove the dummy player \"" + playerName + "\"";
    }

    public static String ofDummyPlayerListExit(List<String> playerNameList){
        return "Dummy player list(" + playerNameList.size() + "): [" + String.join(", ", playerNameList) + "]";
    }

    public static String ofBlockParseError(String block){
        return "Cannot parse \"" + block + "\" to a block.";
    }

    public static String ofBlockNotSettableError(String block) {
        return block + "is not settable.";
    }

    public static String ofPosArrayLengthError(int length){
        return "Pos array length must be " + length + ".";
    }

    public static String ofPosRangeError(int posDataSize){
        return "Pos index must be 0.." + (posDataSize - 1) + ".";
    }

    public static String ofUndoExit(int undoEditCount){
        if(undoEditCount == 0){
            return "Nothing to undo.";
        }else {
            return "Undid " + undoEditCount + " edit(s).";
        }
    }

    public static String ofRedoExit(int redoEditCount){
        if(redoEditCount == 0){
            return "Nothing to redo.";
        }else {
            return "Redid " + redoEditCount + " edit(s).";
        }
    }

    public static String ofTranslateExit(double dx, double dy, double dz){
        return "Translated (" + dx + ", " + dy + ", "+ dz + ").";
    }

    public static String ofRotateExit(double angle, Axis axis){
        return "Rotated " + angle + "[deg] about " + axis.toString().toLowerCase() + "-axis.";
    }

    public static String ofScaleExit(double factorX, double factorY, double factorZ){
        return "Scaled the selection (" + factorX + ", " + factorY + ", " + factorZ+ ").";
    }

    public static String ofReflectExit(Axis axis){
        return "Reflected the selection about "
                +  axis.toString().toLowerCase() + "-axis.";
    }

    public static String ofShearExit(Axis axis, double factor1, double factor2){
        return "Sheared the selection about "
                +  axis.toString().toLowerCase() + "-axis "
                + "by (" + factor1 + ", " + factor2 + ").";
    }

    public static String ofSetExit(EditExit editExit){
        return "Set " + editExit.blockCount() + " blocks, "
                + editExit.entityCount() + " entities, "
                + editExit.biomeCount() + " biomes.";
    }

    public static String ofCopyExit(EditExit editExit){
        return "Copied " + editExit.blockCount() + " blocks, "
                + editExit.entityCount() + " entities, "
                + editExit.biomeCount() + " biomes.";
    }

    public static String ofCutExit(EditExit editExit){
        return "Cut " + editExit.blockCount() + " blocks, "
                + editExit.entityCount() + " entities, "
                + editExit.biomeCount() + " biomes.";
    }

    public static String ofPosExit(int index, int x, int y, int z){
        return "Set Pos" + index + ": " + "(" + x + ", " + y + ", " + z + ")";
    }

}
