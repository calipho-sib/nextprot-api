package org.nextprot.api.isoform.mapper.domain.impl.exception;

import org.nextprot.api.isoform.mapper.domain.FeatureQueryException;
import org.nextprot.api.isoform.mapper.domain.SingleFeatureQuery;

public class EntryAccessionNotFoundForGeneException extends FeatureQueryException {

    private static final String GENE_NAME = "geneName";

    public EntryAccessionNotFoundForGeneException(SingleFeatureQuery query, String geneName) {

        super(query);

        getReason().addCause(GENE_NAME, geneName);
        getReason().setMessage("cannot find entry accession for gene " + geneName);
    }
}
