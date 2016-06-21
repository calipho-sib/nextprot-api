package com.nextprot.api.isoform.mapper.service.impl;

import org.nextprot.api.commons.bio.variation.ProteinSequenceVariation;
import org.nextprot.api.commons.bio.variation.format.AbstractProteinSequenceVariationFormat;
import org.nextprot.api.commons.bio.variation.format.hgvs.ProteinSequenceVariationHGVSFormat;
import org.nextprot.api.core.dao.EntityName;
import org.nextprot.api.core.domain.Entry;

import java.text.ParseException;
import java.util.List;

/**
 * Parse and provide gene name and protein sequence variant
 */
class GeneVariantParser {

    private final String geneName;
    private final ProteinSequenceVariation proteinSequenceVariation;

    GeneVariantParser(String variant, Entry entry) throws ParseException {

        int colonPosition = variant.lastIndexOf("-");
        String geneName = variant.substring(0, colonPosition);
        String hgvVariant = variant.substring(colonPosition + 1);

        // TODO: checking isoform too (ex: WT1-iso4-p.Phe154Ser vs NX_P19544-4)
        /*if (!validateGeneName(entry, geneName)) {
            throw new NextProtException(entry.getUniqueName() + " does not comes from gene " + geneName);
        }*/

        ProteinSequenceVariationHGVSFormat format = new ProteinSequenceVariationHGVSFormat();
        proteinSequenceVariation = format.parse(hgvVariant, AbstractProteinSequenceVariationFormat.ParsingMode.PERMISSIVE);

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

    public ProteinSequenceVariation getProteinSequenceVariation() {
        return proteinSequenceVariation;
    }

    public String getGeneName() {
        return geneName;
    }
}
