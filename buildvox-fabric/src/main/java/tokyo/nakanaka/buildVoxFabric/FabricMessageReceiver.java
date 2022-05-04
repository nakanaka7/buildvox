package tokyo.nakanaka.buildVoxFabric;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import tokyo.nakanaka.buildVoxCore.MessageReceiver;

/**
 * The implementation of {@link MessageReceiver} for Fabric platform
 */
public class FabricMessageReceiver implements MessageReceiver {
    private ServerCommandSource source;

    /**
     * Constructs an instance from a ServerCommandSource
     * @param source a ServerCommandSource
     */
    public FabricMessageReceiver(ServerCommandSource source) {
        this.source = source;
    }

    @Override
    public void println(String msg) {
        source.sendFeedback(Text.of(msg), false);
    }

}
