package tokyo.nakanaka.buildvox.core.command.bvCommand.brushBindCommand;

import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.BlockSettingOptions;
import tokyo.nakanaka.buildvox.core.command.bvCommand.BvCommand;

@Command(name = "brush-bind",
        mixinStandardHelpOptions = true,
        description = "Binds brush type",
        subcommands = {SphereCommand.class, CylinderCommand.class, ClipboardCommand.class})
public class BrushBindCommand {
    @ParentCommand
    private BvCommand bvCmd;

    @Mixin
    private BlockSettingOptions blockSettingOptions;

    public BvCommand getBvCommand() {
        return bvCmd;
    }

    public BlockSettingOptions getBlockSettingOptions() {
        return blockSettingOptions;
    }

}
