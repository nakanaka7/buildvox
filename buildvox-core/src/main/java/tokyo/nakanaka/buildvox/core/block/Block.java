package tokyo.nakanaka.buildvox.core.block;

import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.Identifiable;

import java.util.Map;

/**
 * Represents a block.
 * @param <S> the state object.
 * @param <E> the entity object.
 */
public interface Block<S extends Block.State, E extends Block.Entity> extends Identifiable<NamespacedId> {
    /** * Represents a block state. */
    interface State {
        /** Gets the state map */
        Map<String, String> getStateMap();
    }

    /** Represents a block entity. */
    interface Entity {
    }

    /**
     * Transforms the state.
     * @param state the original state.
     * @param trans the block transformation.
     * @return the transformed state.
     */
    S transformState(S state, BlockTransformation trans);

    /**
     * Parses the String to a Block.State. It is the inner part of [...].
     * @param s a String.
     * @return the State.
     * @throws IllegalArgumentException if it cannot parse s.
     */
    default S parseState(String s) {
        throw new IllegalArgumentException();
    }

    /**
     * Parses the String to a Block.Entity. It is the inner part of {...}.
     * @param s a String.
     * @return the Entity.
     * @throws IllegalArgumentException if it cannot parse s.
     */
    default E parseEntity(String s) {
        throw new IllegalArgumentException();
    }

}
