package tokyo.nakanaka.buildvox.core.player;

import tokyo.nakanaka.buildvox.core.PlayerEntity;
import tokyo.nakanaka.buildvox.core.particleGui.Color;
import tokyo.nakanaka.buildvox.core.world.World;

import java.util.UUID;

public class DummyPlayer extends Player {
    private String name;

    public DummyPlayer(String name) {
        super(UUID.randomUUID(), new DummyPlayerEntity());
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private static class DummyPlayerEntity implements PlayerEntity {
        @Override
        public void givePosMarker() {
        }

        @Override
        public void spawnParticle(Color color, World world, double x, double y, double z) {
        }

    }

}
