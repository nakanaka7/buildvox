package tokyo.nakanaka.buildVoxCore.command.bvCommand;

import picocli.CommandLine;
import tokyo.nakanaka.buildVoxCore.FeedbackMessage;
import tokyo.nakanaka.buildVoxCore.command.bvCommand.affineTransformCommand.*;
import tokyo.nakanaka.buildVoxCore.command.bvCommand.posArray.PosArrayCommand;
import tokyo.nakanaka.buildVoxCore.math.vector.Vector3i;
import tokyo.nakanaka.buildVoxCore.player.Player;
import tokyo.nakanaka.buildVoxCore.system.BuildVoxSystem;
import tokyo.nakanaka.buildVoxCore.system.DummyPlayerRepository;
import tokyo.nakanaka.buildVoxCore.system.PlayerRepository;
import tokyo.nakanaka.buildVoxCore.world.World;

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
    private UUID playerId;
    private World world;
    private int x;
    private int y;
    private int z;
    
    public static class DummyPlayerIdIterable implements Iterable<String> {
        @Override
        public Iterator<String> iterator() {
            return BuildVoxSystem.DUMMY_PLAYER_REPOSITORY.nameSet().stream().iterator();
        }
    }

    public BvCommand(UUID playerId, World world, int x, int y, int z) {
        this.playerId = playerId;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * ExecutionStrategy of this command. This method is called before command execution.
     * https://picocli.info/#_initialization_before_execution
     */
    public int executionStrategy(CommandLine.ParseResult parseResult){
        PrintWriter err = commandSpec.commandLine().getErr();
        PlayerRepository playerRepo = BuildVoxSystem.PLAYER_REPOSITORY;
        if(playerId != null){
            player = playerRepo.get(playerId);
        }
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

    public Vector3i getExecPos() {
        return new Vector3i(x, y, z);
    }

}
