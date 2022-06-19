package tokyo.nakanaka.buildvox.core.registry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a registry for {@link Entity}
 * @param <E> entity type
 * @param <I> id type of the entity
 */
public class Registry<E extends Entity<I>, I> {
    private final Map<I, E> entityMap = new LinkedHashMap<>();

    /**
     * Checks this registry has the entity
     * @param entity the entity
     * @return true if this registry has the entity, otherwise false
     */
    public boolean contains(E entity) {
        return entityMap.containsKey(entity.getId());
    }

    /**
     * Register the entity. The entity with the same id will be overwritten.
     * @param entity the entity to register
     */
    public void register(E entity) {
        entityMap.put(entity.getId(), entity);
    }

    /**
     * Get the entity with the id
     * @param id the id
     * @return the entity with the id
     */
    public E get(I id) {
        return entityMap.get(id);
    }

    /**
     * Unregister the entity. If the entity with the id is not registered, this will do nothing.
     * @param id the entity id to unregister
     * @return E the entity which was unregistered. If an entity of id was not registered, returns null.
     */
    public E unregister(I id) {
        return entityMap.remove(id);
    }

    /**
     * Get the id list of entities
     * @return the id list of entities
     */
    public List<I> idList() {
        return new ArrayList<>(entityMap.keySet());
    }

}
