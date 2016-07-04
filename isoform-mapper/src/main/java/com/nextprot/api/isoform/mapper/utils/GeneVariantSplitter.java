package com.nextprot.api.isoform.mapper.utils;

import com.google.common.base.Preconditions;
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
public class GeneVariantSplitter {

    private final String geneName;
    private final ProteinSequenceVariation proteinSequenceVariation;

    public GeneVariantSplitter(String variant) throws ParseException {

        String geneName = getGeneName(variant);

        int lastDashPosition = variant.lastIndexOf("-");
        String hgvVariant = variant.substring(lastDashPosition + 1);

        ProteinSequenceVariationHGVSFormat format = new ProteinSequenceVariationHGVSFormat();
        proteinSequenceVariation = format.parse(hgvVariant, AbstractProteinSequenceVariationFormat.ParsingMode.PERMISSIVE);

        this.geneName = geneName;
    }

    public boolean isValidGeneName(Entry entry) {

        List<EntityName> geneNames = entry.getOverview().getGeneNames();

        for (EntityName name : geneNames) {

            if (geneName.startsWith(name.getName())) {
                return true;
            }
        }

        return false;
    }

    public ProteinSequenceVariation getVariant() {
        return proteinSequenceVariation;
    }

    public String getGeneName() {
        return geneName;
    }

    public static String getGeneName(String variant) {

        Preconditions.checkNotNull(variant);

        return variant.substring(0, variant.indexOf("-"));
    }
}
