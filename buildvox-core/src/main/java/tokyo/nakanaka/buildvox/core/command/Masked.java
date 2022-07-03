package tokyo.nakanaka.buildvox.core.command;

import picocli.CommandLine;

public class Masked {
    @CommandLine.Option(names = {"-m", "--masked"}, description = "Skips background block settings.")
    private boolean masked;

    public boolean masked() {
        return masked;
    }

}
