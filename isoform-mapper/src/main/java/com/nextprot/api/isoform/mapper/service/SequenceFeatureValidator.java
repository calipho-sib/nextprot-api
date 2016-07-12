package com.nextprot.api.isoform.mapper.service;

import com.nextprot.api.isoform.mapper.domain.FeatureQuery;
import com.nextprot.api.isoform.mapper.domain.FeatureQueryException;
import com.nextprot.api.isoform.mapper.domain.FeatureQueryResult;
import com.nextprot.api.isoform.mapper.domain.EntryIsoform;

/**
 * Validate a feature on a given neXtProt sequence entry
 */
public interface SequenceFeatureValidator {

    FeatureQueryResult validate(FeatureQuery query, EntryIsoform entryIsoform) throws FeatureQueryException;
}
