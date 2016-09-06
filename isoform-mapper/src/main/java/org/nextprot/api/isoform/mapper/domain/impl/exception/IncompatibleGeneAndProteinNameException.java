package org.nextprot.api.isoform.mapper.domain.impl.exception;

import java.util.List;

import org.nextprot.api.isoform.mapper.domain.FeatureQuery;
import org.nextprot.api.isoform.mapper.domain.FeatureQueryException;

public class IncompatibleGeneAndProteinNameException extends FeatureQueryException {

    private static final String GENE_NAME = "geneName";
    private static final String EXPECTED_GENE_NAMES = "expectedGeneNames";

    public IncompatibleGeneAndProteinNameException(FeatureQuery query, String geneName, List<String> expectedGeneNames) {

        super(query);

        getError().addCause(GENE_NAME, geneName);
        getError().addCause(EXPECTED_GENE_NAMES, expectedGeneNames);
        getError().setMessage("gene->protein incompatibility: protein " + query.getAccession() + " is not compatible with gene " + geneName + " (expected genes: " + expectedGeneNames + ")");
    }
}
