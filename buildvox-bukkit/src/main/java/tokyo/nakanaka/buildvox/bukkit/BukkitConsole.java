package tokyo.nakanaka.buildvox.bukkit;

import org.bukkit.command.ConsoleCommandSender;
import tokyo.nakanaka.buildvox.core.commandSender.AbstractConsole;

public class BukkitConsole extends AbstractConsole {
    private ConsoleCommandSender consoleSender;

    public BukkitConsole(ConsoleCommandSender consoleSender) {
        this.consoleSender = consoleSender;
    }

    @Override
    public void sendMessage(String msg) {
        consoleSender.sendMessage(msg);
    }

}
