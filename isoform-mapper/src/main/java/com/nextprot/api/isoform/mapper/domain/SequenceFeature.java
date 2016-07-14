package com.nextprot.api.isoform.mapper.domain;

import org.nextprot.api.commons.bio.variation.SequenceVariation;
import org.nextprot.api.core.domain.Entry;

/**
 * A sequence feature on an isoform sequence on a specific gene
 */
public interface SequenceFeature {

    /** @return the gene name */
    String getGeneName();

    /** @return the sequence variation */
    SequenceVariation getVariation();

    /** @return true if gene name is referenced in entry */
    boolean isValidGeneName(Entry entry);
}
