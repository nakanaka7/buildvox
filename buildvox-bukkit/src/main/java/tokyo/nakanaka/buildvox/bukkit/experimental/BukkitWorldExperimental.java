package tokyo.nakanaka.buildvox.bukkit.experimental;

import tokyo.nakanaka.buildVoxCore.world.Biome;
import tokyo.nakanaka.buildVoxCore.world.Entity;

import java.util.List;
import java.util.UUID;

class BukkitWorldExperimental {
    private org.bukkit.World originalWorld;
    private UUID uuid;

    public BukkitWorldExperimental(org.bukkit.World original){
        this.originalWorld = original;
        this.uuid = original.getUID();
    }

    public int biomeDimension() {
        return 3;
    }

    public Biome getBiome3d(int x, int y, int z) {
        org.bukkit.block.Block voxel = originalWorld.getBlockAt(x, y, z);
        return new BukkitBiome(voxel.getBiome());
    }

    public void setBiome3d(int x, int y, int z, Biome biome) {
        if(biome instanceof BukkitBiome bukkitBiome){
            org.bukkit.block.Block voxel = originalWorld.getBlockAt(x, y, z);
            org.bukkit.block.Biome bio = bukkitBiome.getOriginal();
            voxel.setBiome(bio);
        }else{
            throw new IllegalArgumentException();
        }
    }

    public Biome getBiome2d(int x, int z) {
        throw new UnsupportedOperationException();
    }

    public void setBiome2d(int x, int z, Biome biome) {
        throw new UnsupportedOperationException();
    }

    public Entity[] getEntities() {
        List<org.bukkit.entity.Entity> originalEntityList = this.originalWorld.getEntities();
        Entity[] entities = new Entity[originalEntityList.size()];
        for(int i = 0; i < originalEntityList.size(); ++i){
            entities[i] = new BukkitEntity(originalEntityList.get(i));
        }
        return entities;
    }

    public void setEntity(double x, double y, double z, Entity entity) {

    }

}
