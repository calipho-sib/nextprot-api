package com.nextprot.api.isoform.mapper.service;

import com.nextprot.api.isoform.mapper.domain.FeatureQuery;
import com.nextprot.api.isoform.mapper.domain.FeatureQueryException;
import com.nextprot.api.isoform.mapper.domain.FeatureQueryResult;
import com.nextprot.api.isoform.mapper.utils.EntryIsoform;

/**
 * Validate a feature on a given neXtProt entry
 */
public interface FeatureValidator {

    FeatureQueryResult validate(FeatureQuery query, EntryIsoform entryIsoform) throws FeatureQueryException;
    //GeneVariantPair.GeneFeaturePairParser newGeneFeatureParser();
}
