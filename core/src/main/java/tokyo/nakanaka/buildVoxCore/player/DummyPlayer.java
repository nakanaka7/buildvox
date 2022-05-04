package tokyo.nakanaka.buildVoxCore.player;

import tokyo.nakanaka.buildVoxCore.PlayerEntity;
import tokyo.nakanaka.buildVoxCore.particleGui.Color;
import tokyo.nakanaka.buildVoxCore.world.World;

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
