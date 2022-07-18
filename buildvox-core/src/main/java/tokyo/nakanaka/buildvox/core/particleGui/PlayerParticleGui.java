package tokyo.nakanaka.buildvox.core.particleGui;

public class PlayerParticleGui {
    private boolean particleGuiVisible;
    private ParticleGui particleGui;

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
