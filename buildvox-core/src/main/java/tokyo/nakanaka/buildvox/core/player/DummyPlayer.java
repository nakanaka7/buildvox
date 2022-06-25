package tokyo.nakanaka.buildvox.core.player;

import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.particleGui.Color;
import tokyo.nakanaka.buildvox.core.Identifiable;
import tokyo.nakanaka.buildvox.core.world.World;

import java.util.UUID;

/**
 * Represents a dummy player. A random uuid will be given to this player.
 */
public class DummyPlayer extends Player implements Identifiable<String> {
    private String name;

    /**
     * Creates a new object.
     * @param name the name of dummy player
     */
    public DummyPlayer(String name) {
        super(new DummyPlayerEntity());
        this.name = name;
    }

    /**
     * Get the name of this dummy player.
     * @return the name of this dummy player
     */
    @Override
    public String getId() {
        return name;
    }

    private static class DummyPlayerEntity implements PlayerEntity {
        private UUID id = UUID.randomUUID();

        public UUID getId() {
            return id;
        }

        public void println(String msg) {
        }

        @Override
        public World getWorld() {
            return null;
        }

        @Override
        public Vector3i getBlockPos() {
            return new Vector3i(0, 0, 0);
        }

        @Override
        public void givePosMarker() {
        }

        @Override
        public void spawnParticle(Color color, World world, double x, double y, double z) {
        }

    }

}
