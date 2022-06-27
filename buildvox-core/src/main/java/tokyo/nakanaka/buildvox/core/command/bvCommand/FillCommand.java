package tokyo.nakanaka.buildvox.core.command.bvCommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import tokyo.nakanaka.buildvox.core.Messages;
import tokyo.nakanaka.buildvox.core.edit.EditExit;
import tokyo.nakanaka.buildvox.core.command.IllegalPosException;
import tokyo.nakanaka.buildvox.core.command.MissingPosDataException;
import tokyo.nakanaka.buildvox.core.command.PosDataSizeException;
import tokyo.nakanaka.buildvox.core.command.completionCandidates.BlockCandidates;
import tokyo.nakanaka.buildvox.core.command.mixin.IntegrityMixin;
import tokyo.nakanaka.buildvox.core.command.mixin.shapeMixin.*;
import tokyo.nakanaka.buildvox.core.command.typeConverter.VoxelBlockConverter;
import tokyo.nakanaka.buildvox.core.edit.PlayerEdits;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.player.Player;
import tokyo.nakanaka.buildvox.core.selection.Selection;
import tokyo.nakanaka.buildvox.core.selection.SelectionCreations;
import tokyo.nakanaka.buildvox.core.world.VoxelBlock;

import java.io.PrintWriter;

@CommandLine.Command(name = "fill", mixinStandardHelpOptions = true,
        description = "Fill blocks into the selection or specified shape region."
)
public class FillCommand implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(FillCommand.class);
    private static final String DESC_HEAD = "Fill blocks in ";

    @CommandLine.Spec
    private CommandLine.Model.CommandSpec commandSpec;
    @CommandLine.ParentCommand
    private BvCommand bvCmd;
    @CommandLine.Mixin
    private IntegrityMixin integrityMixin;
    @CommandLine.Parameters(description = "The block.", completionCandidates = BlockCandidates.class, converter = VoxelBlockConverter.class)
    private VoxelBlock block;
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
    @CommandLine.Option(names = {"-r", "--replace"}, description = "The block to replace", completionCandidates = BlockCandidates.class, converter = VoxelBlockConverter.class)
    private VoxelBlock filter;

    @Override
    public void run() {
        run(this::createSelection);
    }

    private void runSubcommand(ShapeMixin shapeMixin) {
        run(() -> createSubcommandSelection(shapeMixin));
    }

    private void run(SelectionFactory selectionFactory) {
        PrintWriter out = commandSpec.commandLine().getOut();
        PrintWriter err = commandSpec.commandLine().getErr();
        Player player = bvCmd.getTargetPlayer();
        try{
            integrityMixin.checkValue();
        }catch (IllegalStateException ex) {
            err.println(ex.getMessage());
            return;
        }
        Selection selection;
        try{
            selection = selectionFactory.create();
        }catch (MissingPosDataException ex) {
            err.println(Messages.INCOMPLETE_POS_DATA_ERROR);
            return;
        }catch (PosDataSizeException ex) {
            err.println(Messages.ofPosArrayLengthError(ex.getAcceptableSize()));
            return;
        }catch (IllegalPosException ex) {
            err.println(Messages.POS_DATA_ERROR);
            return;
        }catch (IllegalShapeArgumentException ex) {
            err.println("Invalid shape argument(s)");
            return;
        }
        double integrity = integrityMixin.integrity();
        EditExit exit;
        if(filter == null) {
            exit = PlayerEdits.fill(player, selection, block, integrity);
        }else {
            exit = PlayerEdits.replace(player, selection, filter, block, integrity);
        }
        out.println(Messages.ofSetExit(exit));
    }

    private interface SelectionFactory {
        /** @throws PosDataSizeException
         * @throws MissingPosDataException
         * @throws IllegalShapeArgumentException
         */
        Selection create();
    }

    public static class IllegalShapeArgumentException extends RuntimeException {

    }

    /**
     * @throws MissingPosDataException
     */
    private Selection createSelection() {
        Player player = bvCmd.getTargetPlayer();
        Selection selection = player.getSelection();
        if (selection != null) {
            return selection;
        }
        Vector3i[] posArray = player.getPosArrayClone();
        try{
            return SelectionCreations.createDefault(posArray);
        }catch (IllegalArgumentException ex) {
            throw new MissingPosDataException();
        }
    }

    /**
     *
     * @throws PosDataSizeException
     * @throws MissingPosDataException
     * @throws IllegalShapeArgumentException
     */
    private Selection createSubcommandSelection(ShapeMixin shapeMixin) {
        Vector3i[] posData = bvCmd.getTargetPlayer().getPosArrayClone();
        try {
            return shapeMixin.createSelection(posData);
        }catch (IllegalStateException ex) {
            throw new IllegalShapeArgumentException();
        }
    }

}
