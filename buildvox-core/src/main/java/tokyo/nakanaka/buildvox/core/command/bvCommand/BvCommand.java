package tokyo.nakanaka.buildvox.core.command.bvCommand;

import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.Messages;
import tokyo.nakanaka.buildvox.core.command.bvCommand.affineTransformCommand.*;
import tokyo.nakanaka.buildvox.core.command.bvCommand.posArray.PosArrayCommand;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.player.Player;
import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;
import tokyo.nakanaka.buildvox.core.World;

import java.io.PrintWriter;
import java.util.Iterator;

@Command(name = "bv",
        mixinStandardHelpOptions = true,
        description = "The root command of BuildVox.",
        subcommands = {
                PosCommand.class, PosMarkerCommand.class, PosArrayCommand.class,
                ApplyPhysicsCommand.class,
                BackgroundCommand.class,
                UndoCommand.class, RedoCommand.class,
                TranslateCommand.class, RotateCommand.class, ScaleCommand.class, ReflectCommand.class, ShearCommand.class,
                FillCommand.class, ReplaceCommand.class,
                CopyCommand.class, CutCommand.class, PasteCommand.class,
                RepeatCommand.class,
        })
public class BvCommand implements Runnable {
    @Spec
    private Model.CommandSpec commandSpec;
    @Option(names = {"-d", "--dummy"},
            description = "Set a dummy player of this command execution. See also /bvd command help.",
            completionCandidates = DummyPlayerIdIterable.class
    )
    private String dummyPlayer;

    private Player targetPlayer;
    private World execWorld;
    private final Vector3i execPos;
    
    public static class DummyPlayerIdIterable implements Iterable<String> {
        @Override
        public Iterator<String> iterator() {
            return BuildVoxSystem.getDummyPlayerRegistry().idList().stream().iterator();
        }
    }

    /***
     * @param execPlayer the player who executed this command. It may be null.
     * @param execWorld the world of this command execution.
     * @param execPos the block position of this command execution.
     */
    public BvCommand(Player execPlayer, World execWorld, Vector3i execPos) {
        this.targetPlayer = execPlayer;
        this.execWorld = execWorld;
        this.execPos = execPos;
    }

    /**
     * ExecutionStrategy of this command. This method is called before command execution.
     * https://picocli.info/#_initialization_before_execution
     */
    public int executionStrategy(ParseResult parseResult){
        PrintWriter err = commandSpec.commandLine().getErr();
        //-d
        if(dummyPlayer != null){
            var dummyPlayerRepo = BuildVoxSystem.getDummyPlayerRegistry();
            if(!dummyPlayerRepo.idList().contains(dummyPlayer)){
                err.println(Messages.NOT_FOUND_DUMMY_PLAYER_ERROR);
                return 0;
            }
            targetPlayer = dummyPlayerRepo.get(dummyPlayer);
        }
        if(targetPlayer == null) {
            err.println(Messages.SESSION_NULL_ERROR);
            return 0;
        }
        if(execWorld != targetPlayer.getEditWorld() ) {
            targetPlayer.setEditWorld(execWorld);
        }
        return new RunLast().execute(parseResult);
    }
    
    @Override
    public void run(){
    }

    /**
     *  Get the target player of this command. The player may not be the execPlayer because of specifying a
     *  dummy player.
     */
    public Player getPlayer() {
        return targetPlayer;
    }

    /** Get the execution block position of this command */
    public Vector3i getExecutionPos() {
        return execPos;
    }

}
