package tokyo.nakanaka.buildvox.core.system;

import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.player.RealPlayer;

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
     * Creates a new instance.
     * @param worldId the id of the world of the command execution.
     * @param pos the position of the command execution.
     * @param messenger the messenger of the command feedback.
     */
    public CommandSource(NamespacedId worldId, Vector3i pos, Messenger messenger) {
        this(worldId, pos, messenger, null);
    }
    /**
     * Creates a new instance for the player id.
     * @param playerId the id of the player who executed the command.
     */
    public static CommandSource newInstance(UUID playerId) {
        RealPlayer player = BuildVoxSystem.getRealPlayerRegistry().get(playerId);
        NamespacedId worldId = player.getPlayerEntity().getWorldId();
        Vector3i pos = player.getPlayerEntity().getBlockPos();
        Messenger messenger = player.getMessenger();
        return new CommandSource(worldId, pos, messenger, playerId);
    }

    /** Gets the command execution world id. */
    public NamespacedId worldId() {
        return worldId;
    }

    /** Gets the command execution position. */
    public Vector3i pos() {
        return pos;
    }

    /** Gets the messenger. */
    public Messenger messenger() {
        return messenger;
    }

    /** Gets the player id. Nullable. */
    public UUID playerId() {
        return playerId;
    }

}
