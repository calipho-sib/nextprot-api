package org.nextprot.api.isoform.mapper.domain.query;

import java.io.Serializable;
import java.util.List;


public interface FeatureQuery extends Serializable {

    /** @return a list of feature */
    List<String> getFeatureList();

    /** @return the feature type (annotation category such as variant, mutagenesis, ...) */
    String getFeatureType();

    /** @return entry accession (or null if not defined) */
    String getAccession();

    /** @throws FeatureQueryException if invalid query */
    void checkFeatureQuery() throws FeatureQueryException;
}
