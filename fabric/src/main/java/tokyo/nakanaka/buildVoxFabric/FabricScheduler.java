package tokyo.nakanaka.buildVoxFabric;

import tokyo.nakanaka.buildVoxCore.Scheduler;

import java.util.*;

/**
 * A scheduler for Fabric platform. This class uses a mixin class, {@link tokyo.nakanaka.buildVoxFabric.mixin.MinecraftServerMixin}.
 * Hence, for using this class, the mod have to be effective. This class is singleton.
 */
public class FabricScheduler implements Scheduler {
    private Map<UUID, Runnable> runnableMap = new HashMap<>();
    private Map<UUID, Integer> leftTickMap = new HashMap<>();
    private static FabricScheduler instance;

    private FabricScheduler() {
    }

    /**
     * Initialize the scheduler.
     */
    public static void initialize() {
        instance = new FabricScheduler();
    }

    /**
     * Get the instance of this class.
     * @return the instance of this class.
     * @throws IllegalStateException if initialize() has never been called.
     */
    public static FabricScheduler getInstance() {
        if(instance == null)throw new IllegalStateException();
        return instance;
    }

    @Override
    public void schedule(Runnable runnable, int i) {
        if(i == 0){
            runnable.run();
        }
        UUID id = UUID.randomUUID();
        runnableMap.put(id, runnable);
        leftTickMap.put(id, i);
    }

    //should be called by only MinecraftServerMixin
    public static void runForCurrentTick(){
        //decrement all the left tick of left tick map
        for(Map.Entry<UUID, Integer> entry : instance.leftTickMap.entrySet()){
            UUID id = entry.getKey();
            int leftTick = entry.getValue();
            instance.leftTickMap.put(id, --leftTick);
        }
        //Find the runnables which left tick are 0 (or less than 0; not reachable)
        List<UUID> idList = new ArrayList<>();
        for(UUID id : instance.leftTickMap.keySet()){
            int leftTick = instance.leftTickMap.get(id);
            if(leftTick <= 0) {
                idList.add(id);
            }
        }
        //Run the runnable which left tick are 0 and remove the entries from the both map.
        for(UUID id : idList){
            instance.runnableMap.get(id).run();
            instance.runnableMap.remove(id);
            instance.leftTickMap.remove(id);
        }
    }

}
