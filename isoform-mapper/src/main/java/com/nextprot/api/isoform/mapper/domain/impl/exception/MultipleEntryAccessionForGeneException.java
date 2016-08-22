package com.nextprot.api.isoform.mapper.domain.impl.exception;

import com.nextprot.api.isoform.mapper.domain.FeatureQuery;
import com.nextprot.api.isoform.mapper.domain.FeatureQueryException;

import java.util.Collection;

public class MultipleEntryAccessionForGeneException extends FeatureQueryException {

    private static final String GENE_NAME = "geneName";
    private static final String FOUND_ACCESSIONS = "foundAccessions";

    public MultipleEntryAccessionForGeneException(FeatureQuery query, String geneName, Collection<String> accessions) {

        super(query);

        getError().addCause(GENE_NAME, geneName);
        getError().addCause(FOUND_ACCESSIONS, accessions);
        getError().setMessage("multiple accessions: too many entry accessions found for gene " + geneName+": "+accessions);
    }
}
