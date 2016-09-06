package org.nextprot.api.isoform.mapper.domain.impl.exception;

import java.util.Collection;

import org.nextprot.api.isoform.mapper.domain.FeatureQuery;
import org.nextprot.api.isoform.mapper.domain.FeatureQueryException;

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
