package org.nextprot.api.commons.bio.variation;

/**
 * A sequence change affecting proteins
 *
 * Created by fnikitin on 10/07/15.
 */
public interface ProteinSequenceChange<V> {

    /**
     * Get value materializing the effect of the change else null
     * @return a generic value
     */
    V getValue();
}
