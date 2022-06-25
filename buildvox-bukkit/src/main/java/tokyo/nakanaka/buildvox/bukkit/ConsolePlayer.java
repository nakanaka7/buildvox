package tokyo.nakanaka.buildvox.bukkit;

import org.bukkit.command.ConsoleCommandSender;
import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;
import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.particleGui.Color;
import tokyo.nakanaka.buildvox.core.player.PlayerEntity;
import tokyo.nakanaka.buildvox.core.player.RealPlayer;
import tokyo.nakanaka.buildvox.core.world.World;

import java.util.UUID;

public class ConsolePlayer extends RealPlayer {
    private ConsolePlayer(PlayerEntity playerEntity) {
        super(playerEntity);
    }

    public static ConsolePlayer newInstance(ConsoleCommandSender sender) {
        UUID id = UUID.randomUUID();
        PlayerEntity playerEntity = new PlayerEntity() {
            @Override
            public UUID getId() {
                return id;
            }
            @Override
            public void println(String msg) {
                sender.sendMessage(msg);
            }
            @Override
            public Vector3i getBlockPos() {
                return Vector3i.ZERO;
            }
            @Override
            public World getWorld() {
                return BuildVoxSystem.getWorldRegistry().get(NamespacedId.valueOf("world"));
            }
            @Override
            public NamespacedId getWorldId() {
                return new NamespacedId("world");
            }
            @Override
            public void givePosMarker() {
            }
            @Override
            public void spawnParticle(Color color, World world, double x, double y, double z) {
            }
        };
        return new ConsolePlayer(playerEntity);
    }

}
