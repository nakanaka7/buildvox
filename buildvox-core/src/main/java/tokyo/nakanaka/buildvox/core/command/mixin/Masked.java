package tokyo.nakanaka.buildvox.core.command.mixin;

import picocli.CommandLine;

public class Masked {
    @CommandLine.Option(names = {"-m", "--masked"},
            description = "Skips background block settings.",
            scope = CommandLine.ScopeType.INHERIT)
    private boolean masked;

    public boolean masked() {
        return masked;
    }

    public void setMasked(boolean masked) {
        this.masked = masked;
    }

}
