package com.nextprot.api.isoform.mapper.domain.impl;

import com.nextprot.api.isoform.mapper.domain.FeatureQuery;
import com.nextprot.api.isoform.mapper.domain.MappedIsoformsFeatureError;

import java.util.List;

public class IncompatibleGeneAndProteinName extends MappedIsoformsFeatureError {

    private static final String GENE_NAME = "geneName";
    private static final String EXPECTED_GENE_NAMES = "expectedGeneNames";

    public IncompatibleGeneAndProteinName(FeatureQuery query, String geneName, List<String> expectedGeneNames) {

        super(query);

        getError().addCause(GENE_NAME, geneName);
        getError().addCause(EXPECTED_GENE_NAMES, expectedGeneNames);
        getError().setMessage("gene/protein incompatibility: protein " + query.getAccession() + " is not compatible with gene " + geneName + " (expected genes: " + expectedGeneNames + ")");
    }
}
