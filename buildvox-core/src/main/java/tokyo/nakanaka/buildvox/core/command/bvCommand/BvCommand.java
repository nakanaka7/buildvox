package tokyo.nakanaka.buildvox.core.command.bvCommand;

import picocli.CommandLine;
import tokyo.nakanaka.buildvox.core.FeedbackMessage;
import tokyo.nakanaka.buildvox.core.command.bvCommand.affineTransformCommand.*;
import tokyo.nakanaka.buildvox.core.command.bvCommand.posArray.PosArrayCommand;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.player.Player;
import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;
import tokyo.nakanaka.buildvox.core.system.DummyPlayerRepository;
import tokyo.nakanaka.buildvox.core.system.PlayerRepository;
import tokyo.nakanaka.buildvox.core.world.World;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.UUID;

@CommandLine.Command(name = "bv",
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
                SelectCommand.class
        })
public class BvCommand implements Runnable {
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec commandSpec;
    @CommandLine.Option(names = {"-d", "--dummy"},
            description = "Set a dummy player of this command execution. See also /bvd command help.",
            completionCandidates = DummyPlayerIdIterable.class
    )
    private String dummyPlayer;

    private Player player;
    private World world;
    private final Vector3i execPos;
    
    public static class DummyPlayerIdIterable implements Iterable<String> {
        @Override
        public Iterator<String> iterator() {
            return BuildVoxSystem.DUMMY_PLAYER_REPOSITORY.nameSet().stream().iterator();
        }
    }

    public BvCommand(Player execPlayer, World execWorld, Vector3i execPos) {
        this.player = execPlayer;
        this.world = execWorld;
        this.execPos = execPos;
    }

    /**
     * ExecutionStrategy of this command. This method is called before command execution.
     * https://picocli.info/#_initialization_before_execution
     */
    public int executionStrategy(CommandLine.ParseResult parseResult){
        PrintWriter err = commandSpec.commandLine().getErr();
        //-d
        if(dummyPlayer != null){
            DummyPlayerRepository dummyPlayerRepo = BuildVoxSystem.DUMMY_PLAYER_REPOSITORY;
            if(!dummyPlayerRepo.nameSet().contains(dummyPlayer)){
                err.println(FeedbackMessage.NOT_FOUND_DUMMY_PLAYER_ERROR);
                return 0;
            }
            player = dummyPlayerRepo.get(dummyPlayer);
        }
        if(player == null) {
            err.println(FeedbackMessage.SESSION_NULL_ERROR);
            return 0;
        }
        if(world != player.getWorld() ) {
            player.setWorldWithPosArrayClearedAndSelectionNull(world);
        }
        return new CommandLine.RunLast().execute(parseResult);
    }
    
    @Override
    public void run(){
    }

    public Player getPlayer() {
        return player;
    }

    /** Get the execution block position of this command */
    public Vector3i getExecPos() {
        return execPos;
    }

}
