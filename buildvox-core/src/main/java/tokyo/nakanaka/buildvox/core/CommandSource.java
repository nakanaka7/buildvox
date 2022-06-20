package tokyo.nakanaka.buildvox.core;

import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.player.RealPlayer;
import tokyo.nakanaka.buildvox.core.world.World;

import java.util.UUID;

/**
 * Represents a command source.
 */
public class CommandSource {
    private final NamespacedId worldId;
    private final Vector3i pos;
    private final Messenger messenger;
    private final UUID playerId;

    private CommandSource(NamespacedId worldId, Vector3i pos, Messenger messenger, UUID playerId) {
        this.worldId = worldId;
        this.pos = pos;
        this.messenger = messenger;
        this.playerId = playerId;
    }

    /**
     * @param worldId the id of the world of the command execution.
     * @param pos the position of the command execution.
     * @param messenger the messenger of the command feedback.
     */
    public CommandSource(NamespacedId worldId, Vector3i pos, Messenger messenger) {
        this(worldId, pos, messenger, null);
    }
    /**
     * @param playerId the id of the player who executed the command.
     */
    public static CommandSource newInstance(UUID playerId) {
        RealPlayer player = BuildVoxSystem.getRealPlayerRegistry().get(playerId);
        World world = player.getWorld();
        NamespacedId worldId = world.getId();
        Vector3i pos = player.getBlockPos();
        Messenger messenger = new Messenger() {
            @Override
            public void sendOutMessage(String msg) {
                player.sendOutMessage(msg);
            }
            @Override
            public void sendErrMessage(String msg) {
                player.sendErrMessage(msg);
            }
        };
        return new CommandSource(worldId, pos, messenger, playerId);
    }

    public NamespacedId worldId() {
        return worldId;
    }

    public Vector3i pos() {
        return pos;
    }

    public Messenger messenger() {
        return messenger;
    }

    public UUID playerId() {
        return playerId;
    }

}
