package org.nextprot.api.isoform.mapper.domain.impl.exception;

import org.nextprot.api.isoform.mapper.domain.FeatureQuery;
import org.nextprot.api.isoform.mapper.domain.FeatureQueryException;

public class EntryAccessionNotFoundForGeneException extends FeatureQueryException {

    private static final String GENE_NAME = "geneName";

    public EntryAccessionNotFoundForGeneException(FeatureQuery query, String geneName) {

        super(query);

        getError().addCause(GENE_NAME, geneName);
        getError().setMessage("cannot find entry accession for gene " + geneName);
    }
}
