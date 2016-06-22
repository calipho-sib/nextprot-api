package com.nextprot.api.isoform.mapper.utils;

import org.nextprot.api.commons.bio.variation.ProteinSequenceVariation;
import org.nextprot.api.commons.bio.variation.format.AbstractProteinSequenceVariationFormat;
import org.nextprot.api.commons.bio.variation.format.hgvs.ProteinSequenceVariationHGVSFormat;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.dao.EntityName;

import java.text.ParseException;
import java.util.List;

/**
 * Parse and provide gene name and protein sequence variant
 */
public class GeneVariantParser {

    private final String geneName;
    private final ProteinSequenceVariation proteinSequenceVariation;

    public GeneVariantParser(String variant, EntryIsoform entryIsoform) throws ParseException {

        int colonPosition = variant.lastIndexOf("-");
        String geneName = variant.substring(0, variant.indexOf("-"));
        String hgvVariant = variant.substring(colonPosition + 1);

        if (!validateGeneName(entryIsoform, geneName)) {
            throw new NextProtException(entryIsoform.getEntry().getUniqueName() + " does not comes from gene " + geneName);
        }

        ProteinSequenceVariationHGVSFormat format = new ProteinSequenceVariationHGVSFormat();
        proteinSequenceVariation = format.parse(hgvVariant, AbstractProteinSequenceVariationFormat.ParsingMode.PERMISSIVE);

        this.geneName = geneName;
    }

    private boolean validateGeneName(EntryIsoform entryIsoform, String geneName) {

        List<EntityName> geneNames = entryIsoform.getEntry().getOverview().getGeneNames();

        for (EntityName name : geneNames) {

            if (geneName.startsWith(name.getName())) {
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
