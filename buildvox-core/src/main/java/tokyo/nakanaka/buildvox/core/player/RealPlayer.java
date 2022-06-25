package tokyo.nakanaka.buildvox.core.player;

import tokyo.nakanaka.buildvox.core.Identifiable;

import java.util.UUID;

/**
 * Represents a real player.
 */
public class RealPlayer extends Player implements Identifiable<UUID> {
    private UUID id;

    /**
     * Creates a new instance
     * @param playerEntity a player entity
     */
    public RealPlayer(PlayerEntity playerEntity) {
        super(playerEntity);
        this.id = playerEntity.getId();
    }

    @Override
    public UUID getId() {
        return id;
    }

}
