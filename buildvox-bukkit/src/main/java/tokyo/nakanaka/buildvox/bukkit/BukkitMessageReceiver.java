package tokyo.nakanaka.buildvox.bukkit;

import org.bukkit.command.CommandSender;
import tokyo.nakanaka.buildVoxCore.MessageReceiver;

/**
 * The class which implements {@link MessageReceiver} for Bukkit Platform
 */
public class BukkitMessageReceiver implements MessageReceiver {
    private CommandSender cmdSender;

    /**
     * Constructs an instance from a CommandSender
     * @param cmdSender a CommandSender
     */
    public BukkitMessageReceiver(CommandSender cmdSender) {
        this.cmdSender = cmdSender;
    }

    @Override
    public void println(String msg) {
        cmdSender.sendMessage(msg);
    }

}
