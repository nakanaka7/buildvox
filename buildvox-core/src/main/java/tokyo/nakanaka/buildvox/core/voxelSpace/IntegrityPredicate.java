package tokyo.nakanaka.buildvox.core.voxelSpace;

import java.util.function.Predicate;

/** The predicate function of integrity.
 *  {@link <a href="https://www.computerhope.com/jargon/i/integrit.htm">integrity</a>}
 **/
public class IntegrityPredicate<B> implements Predicate<B> {
    private final double integrity;

    /**
     * @throws IllegalArgumentException if integrity is less than 0 or larger than 1
     */
    public IntegrityPredicate(double integrity) {
        if(integrity < 0 || 1 < integrity)throw new IllegalArgumentException();
        this.integrity = integrity;
    }

    @Override
    public boolean test(B b) {
        return Math.random() < integrity;
    }

}
