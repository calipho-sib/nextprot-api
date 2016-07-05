package com.nextprot.api.isoform.mapper.service;

import com.nextprot.api.isoform.mapper.domain.FeatureQuery;
import com.nextprot.api.isoform.mapper.domain.MappedIsoformsFeatureResult;
import com.nextprot.api.isoform.mapper.utils.EntryIsoform;

/**
 * Validate a feature on a given neXtProt entry
 */
public interface FeatureValidator {

    MappedIsoformsFeatureResult validate(FeatureQuery query, EntryIsoform entryIsoform);
}
