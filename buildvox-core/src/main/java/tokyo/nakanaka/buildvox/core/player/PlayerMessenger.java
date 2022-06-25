package tokyo.nakanaka.buildvox.core.player;

import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;
import tokyo.nakanaka.buildvox.core.system.Messenger;

/** The messenger of player entity */
public class PlayerMessenger implements Messenger {
    private PlayerEntity playerEntity;

    /**
     * Creates a new instance.
     * @param playerEntity the player entity.
     */
    public PlayerMessenger(PlayerEntity playerEntity) {
        this.playerEntity = playerEntity;
    }

    @Override
    public void sendOutMessage(String msg) {
        playerEntity.println(BuildVoxSystem.getOutColor() + msg);
    }

    @Override
    public void sendErrMessage(String msg) {
        playerEntity.println(BuildVoxSystem.getErrColor() + msg);
    }

}
