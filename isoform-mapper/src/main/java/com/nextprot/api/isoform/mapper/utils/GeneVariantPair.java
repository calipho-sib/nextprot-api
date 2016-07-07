package com.nextprot.api.isoform.mapper.utils;

import com.google.common.base.Preconditions;
import com.nextprot.api.isoform.mapper.domain.GeneFeaturePair;
import com.nextprot.api.isoform.mapper.domain.IsoformFeature;
import com.nextprot.api.isoform.mapper.domain.impl.VariantFeature;
import org.nextprot.api.commons.bio.variation.seq.format.AbstractProteinSequenceVariationFormat;
import org.nextprot.api.commons.bio.variation.seq.format.hgvs.ProteinSequenceVariationHGVSFormat;
import org.nextprot.api.core.dao.EntityName;
import org.nextprot.api.core.domain.Entry;

import java.text.ParseException;
import java.util.List;

/**
 * Parse and provide gene name and protein sequence variant
 */
public class GeneVariantPair implements GeneFeaturePair {

    private final String geneName;
    private final IsoformFeature variant;

    public GeneVariantPair(String variant) throws ParseException {

        String geneName = getGeneName(variant);

        int lastDashPosition = variant.lastIndexOf("-");
        String hgvVariant = variant.substring(lastDashPosition + 1);

        ProteinSequenceVariationHGVSFormat format = new ProteinSequenceVariationHGVSFormat();

        this.variant = new VariantFeature(format.parse(hgvVariant, AbstractProteinSequenceVariationFormat.ParsingMode.PERMISSIVE));
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

    public static String getGeneName(String variant) {

        Preconditions.checkNotNull(variant);

        return variant.substring(0, variant.indexOf("-"));
    }

    @Override
    public String getGeneName() {
        return geneName;
    }

    @Override
    public IsoformFeature getFeature() {
        return variant;
    }

    public interface GeneFeaturePairParser {

        GeneFeaturePair parse(String variant) throws ParseException;
    }

    public static class GeneVariantPairParser implements GeneFeaturePairParser {

        @Override
        public GeneFeaturePair parse(String variant) throws ParseException {

            return new GeneVariantPair(variant);
        }
    }
}
