package org.nextprot.api.isoform.mapper.domain.impl.exception;

import org.nextprot.api.isoform.mapper.domain.query.FeatureQueryException;
import org.nextprot.api.isoform.mapper.domain.query.SingleFeatureQuery;

import java.util.Collection;

public class MultipleEntryAccessionForGeneException extends FeatureQueryException {

    private static final String GENE_NAME = "geneName";
    private static final String FOUND_ACCESSIONS = "foundAccessions";

    public MultipleEntryAccessionForGeneException(SingleFeatureQuery query, String geneName, Collection<String> accessions) {

        super(query);

        getReason().addCause(GENE_NAME, geneName);
        getReason().addCause(FOUND_ACCESSIONS, accessions);
        getReason().setMessage("multiple accessions: too many entry accessions found for gene " + geneName+": "+accessions);
    }
}
