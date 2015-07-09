package org.nextprot.api.commons.bio.mutation;

/**
 * A mutation affecting proteins
 *
 * Created by fnikitin on 10/07/15.
 */
public interface Mutation<V> {

    /**
     * Get value materializing the effect of the mutation else null
     * @return a generic value
     */
    V getValue();
}
