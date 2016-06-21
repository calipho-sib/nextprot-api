package com.nextprot.api.isoform.mapper.service.impl;

import org.nextprot.api.commons.bio.mutation.AbstractProteinMutationFormat;
import org.nextprot.api.commons.bio.mutation.ProteinMutation;
import org.nextprot.api.commons.bio.mutation.hgv.ProteinMutationHGVFormat;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.dao.EntityName;
import org.nextprot.api.core.domain.Entry;

import java.text.ParseException;
import java.util.List;

/**
 * Parse gene name and variant
 */
class GeneVariantParser {

    private final String geneName;
    private final ProteinMutation proteinMutation;
    private final ProteinMutationHGVFormat PROTEIN_MUTATION_HGV_FORMAT = new ProteinMutationHGVFormat();

    GeneVariantParser(String mutation, Entry entry) throws ParseException {

        int colonPosition = mutation.lastIndexOf("-");
        String geneName = mutation.substring(0, colonPosition);
        String hgvMutation = mutation.substring(colonPosition + 1);

        if (!validateGeneName(entry, geneName)) {
            throw new NextProtException(entry.getUniqueName() + " does not comes from gene " + geneName);
        }

        proteinMutation = PROTEIN_MUTATION_HGV_FORMAT.parse(hgvMutation, AbstractProteinMutationFormat.ParsingMode.PERMISSIVE);
        this.geneName = geneName;
    }

    private boolean validateGeneName(Entry entry, String geneName) {

        List<EntityName> geneNames = entry.getOverview().getGeneNames();

        for (EntityName name : geneNames) {

            if (name.getName().equals(geneName)) {
                return true;
            }
        }

        return false;
    }

    public ProteinMutation getProteinMutation() {
        return proteinMutation;
    }

    public String getGeneName() {
        return geneName;
    }
}
