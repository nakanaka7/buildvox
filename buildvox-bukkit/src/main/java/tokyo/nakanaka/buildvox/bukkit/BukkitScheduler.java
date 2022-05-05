package tokyo.nakanaka.buildvox.bukkit;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import tokyo.nakanaka.buildvox.core.Scheduler;

/**
 * The class which implements {@link Scheduler} for Bukkit Platform
 */
public class BukkitScheduler implements Scheduler {
    private JavaPlugin plugin;

    /**
     * Constructs a scheduler from a JavaPlugin
     * @param plugin a JavaPlugin
     */
    public BukkitScheduler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void schedule(Runnable runnable, int tick){
        if(tick < 0){
            throw new IllegalArgumentException();
        }
        new BukkitRunnable(){
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskLater(this.plugin, tick);
    }

}
