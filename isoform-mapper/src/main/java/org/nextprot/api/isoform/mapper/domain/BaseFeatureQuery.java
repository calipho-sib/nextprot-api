package org.nextprot.api.isoform.mapper.domain;

import java.io.Serializable;

public interface BaseFeatureQuery extends Serializable {

    /** @return the feature type (annotation category such as variant, mutagenesis, ...) */
    String getFeatureType();

    /** @return entry accession (or null if not defined) */
    String getAccession();
}
