package tokyo.nakanaka.buildvox.core.command.bvCommand;

import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.command.mixin.Block;
import tokyo.nakanaka.buildvox.core.player.Player;
import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;
import tokyo.nakanaka.buildvox.core.block.VoxelBlock;

import java.io.PrintWriter;

@Command(name = "background", mixinStandardHelpOptions = true,
         description = "Set a background block."
)
public class BackgroundCommand implements Runnable {
    @Spec
    private Model.CommandSpec commandSpec;
    @ParentCommand
    private BvCommand bvCmd;
    @Parameters(arity = "1",
            description = "The block.", completionCandidates = Block.Candidates.class)
    private String block;

    @Override
    public void run() {
        PrintWriter out = commandSpec.commandLine().getOut();
        PrintWriter err = commandSpec.commandLine().getErr();
        Player player = bvCmd.getTargetPlayer();
        VoxelBlock b;
        try {
            b = BuildVoxSystem.parseBlock(this.block);
        }catch (IllegalArgumentException e){
            err.println("Cannot parse \"" + this.block + "\" to a block.");
            return;
        }
        player.setBackgroundBlock(b);
        out.println("Set backgroundblock " + block + ".");
    }

}
