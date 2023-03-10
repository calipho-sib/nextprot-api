package org.nextprot.api.isoform.mapper.domain.impl.exception;

import org.nextprot.api.isoform.mapper.domain.query.FeatureQueryException;
import org.nextprot.api.isoform.mapper.domain.query.SingleFeatureQuery;

import java.util.List;

public class IncompatibleGeneAndProteinNameException extends FeatureQueryException {

    private static final String GENE_NAME = "geneName";
    private static final String EXPECTED_GENE_NAMES = "expectedGeneNames";

    public IncompatibleGeneAndProteinNameException(SingleFeatureQuery query, String geneName, List<String> expectedGeneNames) {

        super(query);

        getReason().addCause(GENE_NAME, geneName);
        getReason().addCause(EXPECTED_GENE_NAMES, expectedGeneNames);
        getReason().setMessage("gene->protein incompatibility: protein " + query.getAccession() + " is not compatible with gene " + geneName + " (expected genes: " + expectedGeneNames + ")");
    }
}
