package com.nextprot.api.isoform.mapper.service.impl;

import org.nextprot.api.commons.bio.mutation.AbstractProteinMutationFormat;
import org.nextprot.api.commons.bio.mutation.ProteinMutation;
import org.nextprot.api.commons.bio.mutation.hgv.ProteinMutationHGVFormat;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.Overview;
import org.nextprot.api.core.service.OverviewService;

import java.text.ParseException;

/**
 * Parse gene name and variant
 */
class GeneVariantParser {

    private final String geneName;
    private final ProteinMutation proteinMutation;
    private final ProteinMutationHGVFormat PROTEIN_MUTATION_HGV_FORMAT = new ProteinMutationHGVFormat();

    GeneVariantParser(String mutation, String nextprotAccession, OverviewService overviewService) throws ParseException {

        int colonPosition = mutation.lastIndexOf("-");
        String geneName = mutation.substring(0, colonPosition);
        String hgvMutation = mutation.substring(colonPosition + 1);

        if (!validateGeneName(nextprotAccession, geneName, overviewService)) {
            throw new NextProtException(nextprotAccession + " does not comes from gene " + geneName);
        }

        proteinMutation = PROTEIN_MUTATION_HGV_FORMAT.parse(hgvMutation, AbstractProteinMutationFormat.ParsingMode.PERMISSIVE);
        this.geneName = geneName;
    }

    private boolean validateGeneName(String nextprotAccession, String geneName, OverviewService overviewService) {

        // 1. get overview of entry, check gene name is as expected
        Overview overview = overviewService.findOverviewByEntry(nextprotAccession);

        // TODO: check if genename contained in gene name list instead
        if (!overview.getMainGeneName().equals(geneName))
            return false;
        return true;
    }

    public ProteinMutation getProteinMutation() {
        return proteinMutation;
    }

    public String getGeneName() {
        return geneName;
    }
}
