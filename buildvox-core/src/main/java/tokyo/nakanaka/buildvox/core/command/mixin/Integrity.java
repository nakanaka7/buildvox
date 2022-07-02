package tokyo.nakanaka.buildvox.core.command.mixin;

import picocli.CommandLine;
import tokyo.nakanaka.buildvox.core.Messages;

public class Integrity {
    @CommandLine.Option(names = {"-i", "--integrity"}, description = "The integrity of block setting.",
            defaultValue = "1")
    private double integrity;

    public double integrity() {
        return integrity;
    }

    public void checkValue() {
        if(integrity < 0 || 1 < integrity) {
            throw new IllegalStateException(Messages.INTEGRITY_ERROR);
        }
    }

}
