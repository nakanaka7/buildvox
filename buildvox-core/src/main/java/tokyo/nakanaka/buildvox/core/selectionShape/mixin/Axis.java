package tokyo.nakanaka.buildvox.core.selectionShape.mixin;

import picocli.CommandLine;

public class Axis {
    @CommandLine.Option(names = {"--axis"})
    private tokyo.nakanaka.buildvox.core.Axis axis = tokyo.nakanaka.buildvox.core.Axis.Y;

    public tokyo.nakanaka.buildvox.core.Axis axis() {
        return axis;
    }

}
