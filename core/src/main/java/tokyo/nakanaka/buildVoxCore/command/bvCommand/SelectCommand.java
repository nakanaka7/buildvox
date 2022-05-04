package tokyo.nakanaka.buildVoxCore.command.bvCommand;

import picocli.CommandLine;
import tokyo.nakanaka.buildVoxCore.FeedbackMessage;
import tokyo.nakanaka.buildVoxCore.command.IllegalPosException;
import tokyo.nakanaka.buildVoxCore.command.MissingPosDataException;
import tokyo.nakanaka.buildVoxCore.command.PosDataSizeException;
import tokyo.nakanaka.buildVoxCore.command.mixin.shapeMixin.*;
import tokyo.nakanaka.buildVoxCore.player.Player;
import tokyo.nakanaka.buildVoxCore.selection.Selection;
import tokyo.nakanaka.buildVoxCore.world.World;

import java.io.PrintWriter;

@CommandLine.Command(name = "select", mixinStandardHelpOptions = true,
        description = "Select a shape."
)
public class SelectCommand {
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec commandSpec;
    @CommandLine.ParentCommand
    private BvCommand bvCmd;

    private static final String DESC_HEAD = "Select ";
    @CommandLine.Command(description = DESC_HEAD + ConeMixin.DESCRIPTION + ".", mixinStandardHelpOptions = true)
    private void cone(@CommandLine.Mixin ConeMixin coneMixin) {
        runSubcommand(coneMixin);
    }
    @CommandLine.Command(description = DESC_HEAD + CuboidMixin.DESCRIPTION + ".", mixinStandardHelpOptions = true)
    private void cuboid() {
        runSubcommand(new CuboidMixin());
    }
    @CommandLine.Command(description = DESC_HEAD + CylinderMixin.DESCRIPTION + ".", mixinStandardHelpOptions = true)
    private void cylinder(@CommandLine.Mixin CylinderMixin cylinderMixin) {
        runSubcommand(cylinderMixin);
    }
    @CommandLine.Command(description = DESC_HEAD + EllipseMixin.DESCRIPTION + ".", mixinStandardHelpOptions = true)
    private void ellipse() {
        runSubcommand(new EllipseMixin());
    }
    @CommandLine.Command(description = DESC_HEAD + FrameMixin.DESCRIPTION + ".", mixinStandardHelpOptions = true)
    private void frame(@CommandLine.Mixin FrameMixin frameMixin) {
        runSubcommand(frameMixin);
    }
    @CommandLine.Command(description = DESC_HEAD + LineMixin.DESCRIPTION + ".", mixinStandardHelpOptions = true)
    private void line(@CommandLine.Mixin LineMixin lineMixin) {
        runSubcommand(lineMixin);
    }
    @CommandLine.Command(description = DESC_HEAD + PlateMixin.DESCRIPTION + ".", mixinStandardHelpOptions = true)
    private void plate(@CommandLine.Mixin PlateMixin plateMixin) {
        runSubcommand(plateMixin);
    }
    @CommandLine.Command(description = DESC_HEAD + PyramidMixin.DESCRIPTION + ".", mixinStandardHelpOptions = true)
    private void pyramid(@CommandLine.Mixin PyramidMixin pyramidMixin) {
        runSubcommand(pyramidMixin);
    }
    @CommandLine.Command(description = DESC_HEAD + TetrahedronMixin.DESCRIPTION + ".", mixinStandardHelpOptions = true)
    private void tetrahedron() {
        runSubcommand(new TetrahedronMixin());
    }
    @CommandLine.Command(description = DESC_HEAD + TorusMixin.DESCRIPTION + ".", mixinStandardHelpOptions = true)
    private void torus() {
        runSubcommand(new TorusMixin());
    }
    @CommandLine.Command(description = DESC_HEAD + TriangleMixin.DESCRIPTION + ".", mixinStandardHelpOptions = true)
    private void triangle(@CommandLine.Mixin TriangleMixin triangleMixin) {
        runSubcommand(triangleMixin);
    }

    private void runSubcommand(ShapeMixin shapeMixin) {
        PrintWriter out = commandSpec.commandLine().getOut();
        PrintWriter err = commandSpec.commandLine().getErr();
        Player player = bvCmd.getPlayer();
        World world = player.getWorld();
        Selection selection;
        try {
            selection = shapeMixin.createSelection(player.getPosArrayClone());
        }catch (PosDataSizeException ex) {
            err.println(FeedbackMessage.ofPosArrayLengthError(ex.getAcceptableSize()));
            return;
        }catch (MissingPosDataException ex) {
            err.println(FeedbackMessage.INCOMPLETE_POS_DATA_ERROR);
            return;
        }catch (IllegalPosException ex) {
            err.println(FeedbackMessage.POS_DATA_ERROR);
            return;
        }catch (IllegalStateException ex) {
            err.println("Invalid shape argument(s)");
            return;
        }
        player.setSelectionWithPosArrayCleared(world, selection);
        out.println("Selected.");
    }

}
