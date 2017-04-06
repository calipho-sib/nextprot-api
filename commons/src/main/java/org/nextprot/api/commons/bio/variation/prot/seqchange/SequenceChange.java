package org.nextprot.api.commons.bio.variation.prot.seqchange;

/**
 * Description of a sequence change affecting protein sequence
 *
 * Created by fnikitin on 10/07/15.
 */
public interface SequenceChange<V> {

    enum Type {
        DELETION,
        DUPLICATION,
        FRAMESHIFT,
        INSERTION,
        SUBSTITUTION,
        DELETION_INSERTION,
        EXTENSION_INIT,
        EXTENSION_TERM,
        PTM
    }

    /**
     * Get value materializing the effect of the change else null
     * @return a generic value
     */
    V getValue();

    Type getType();
}
