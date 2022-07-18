package tokyo.nakanaka.buildvox.core.particleGui;

import tokyo.nakanaka.buildvox.core.player.Player;

public class PlayerParticleGui {
    private Player player;
    private boolean particleGuiVisible;
    private ParticleGui particleGui;

    public PlayerParticleGui(Player player) {
        this.player = player;
    }

    public boolean isParticleGuiVisible() {
        return particleGuiVisible;
    }

    public PlayerParticleGui setParticleGuiVisible(boolean particleGuiVisible) {
        this.particleGuiVisible = particleGuiVisible;
        return this;
    }

    public ParticleGui getParticleGui() {
        return particleGui;
    }

    public PlayerParticleGui setParticleGui(ParticleGui particleGui) {
        this.particleGui = particleGui;
        return this;
    }

}
