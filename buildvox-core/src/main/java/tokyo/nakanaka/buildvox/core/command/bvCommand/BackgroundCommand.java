package tokyo.nakanaka.buildvox.core.command.bvCommand;

import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.block.VoxelBlock;
import tokyo.nakanaka.buildvox.core.command.mixin.Block;
import tokyo.nakanaka.buildvox.core.player.Player;

import java.io.PrintWriter;

@Command(name = "background", mixinStandardHelpOptions = true,
         description = "Set a background block."
)
public class BackgroundCommand implements Runnable {
    @Spec
    private Model.CommandSpec commandSpec;
    @ParentCommand
    private BvCommand bvCmd;
    @Mixin
    private Block block;

    @Override
    public void run() {
        PrintWriter out = commandSpec.commandLine().getOut();
        Player player = bvCmd.getPlayer();
        VoxelBlock vb = block.block();
        player.setBackgroundBlock(vb);
        out.println("Set background block to " + vb + ".");
    }

}
