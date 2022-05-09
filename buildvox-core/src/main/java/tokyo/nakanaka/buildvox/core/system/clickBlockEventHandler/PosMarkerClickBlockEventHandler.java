package tokyo.nakanaka.buildvox.core.system.clickBlockEventHandler;

import tokyo.nakanaka.buildvox.core.FeedbackMessage;
import tokyo.nakanaka.buildvox.core.MessageReceiver;
import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.player.Player;
import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;
import tokyo.nakanaka.buildvox.core.world.World;

import java.util.UUID;

import static tokyo.nakanaka.buildvox.core.system.BuildVoxSystem.*;

public class PosMarkerClickBlockEventHandler {
    /**
     * Handles a left-clicking block event by pos marker.
     * @param playerId the id of a player who invoked this event.
     * @param worldId the world id of the clicked block.
     * @param x the x-coordinate of the clicked block.
     * @param y the y-coordinate of the clicked block.
     * @param z the z-coordinate of the clicked block.
     * @param messageReceiver the receiver of the feedback message of this event.
     * @throws IllegalArgumentException if the player id is not registered into this class.
     * @throws IllegalArgumentException if the world id is not registered.
     */
    public void onLeft(UUID playerId, NamespacedId worldId, int x, int y, int z, MessageReceiver messageReceiver) {
        if(!BuildVoxSystem.WORLD_REGISTRY.worldIsRegistered(worldId)) {
            throw new IllegalArgumentException();
        }
        World world = BuildVoxSystem.WORLD_REGISTRY.get(worldId);
        Player player = BuildVoxSystem.PLAYER_REPOSITORY.get(playerId);
        if(player == null)throw new IllegalArgumentException();
        Vector3i[] posData = new Vector3i[player.getPosArrayClone().length];
        posData[0] = new Vector3i(x, y, z);
        player.setPosArrayWithSelectionNull(world, posData);
        PARTICLE_GUI_REPOSITORY.update(player);
        messageReceiver.println(config.outColor() + FeedbackMessage.ofPosExit(0, x, y, z));
    }

    /**
     * Handles a right-clicking block event by pos marker.
     * @param playerId the id of a player who invoked this event.
     * @param worldId the world id of the clicked block.
     * @param x the x-coordinate of the clicked block.
     * @param y the y-coordinate of the clicked block.
     * @param z the z-coordinate of the clicked block.
     * @param messageReceiver the receiver of the feedback message of this event.
     * @throws IllegalArgumentException if the player id is not registered into this class.
     * @throws IllegalArgumentException if the world id is not registered.
     */
    public void onRight(UUID playerId, NamespacedId worldId, int x, int y, int z, MessageReceiver messageReceiver) {
        if(!BuildVoxSystem.WORLD_REGISTRY.worldIsRegistered(worldId)) {
            throw new IllegalArgumentException();
        }
        World world = BuildVoxSystem.WORLD_REGISTRY.get(worldId);
        Player player = BuildVoxSystem.PLAYER_REPOSITORY.get(playerId);
        if(player == null)throw new IllegalArgumentException();
        World posOrSelectionWorld = player.getWorld();
        Vector3i[] posData = player.getPosArrayClone();
        if(world != posOrSelectionWorld){
            posData = new Vector3i[player.getPosArrayClone().length];
        }
        int dataSize = posData.length;
        int index = dataSize - 1;
        for(int i = 0; i < dataSize; ++i){
            if (posData[i] == null){
                index = i;
                break;
            }
        }
        posData[index] = new Vector3i(x, y, z);
        player.setPosArrayWithSelectionNull(world, posData);
        PARTICLE_GUI_REPOSITORY.update(player);
        messageReceiver.println(config.outColor() + FeedbackMessage.ofPosExit(index, x, y, z));
    }

}
