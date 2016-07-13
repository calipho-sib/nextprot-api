package com.nextprot.api.isoform.mapper.domain;

import org.nextprot.api.commons.bio.variation.SequenceVariation;
import org.nextprot.api.core.domain.Entry;

public interface GeneVariationPair {

    String getGeneName();
    SequenceVariation getVariation();
    boolean isValidGeneName(Entry entry);
}
