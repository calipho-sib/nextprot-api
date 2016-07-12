package com.nextprot.api.isoform.mapper.domain.impl;

import com.google.common.base.Preconditions;
import com.nextprot.api.isoform.mapper.domain.GeneVariationPair;
import org.nextprot.api.commons.bio.variation.SequenceVariation;
import org.nextprot.api.commons.bio.variation.SequenceVariationFormat;
import org.nextprot.api.core.dao.EntityName;
import org.nextprot.api.core.domain.Entry;

import java.text.ParseException;
import java.util.List;

/**
 * Parse and provide gene name and protein sequence variant
 */
public abstract class GeneFeaturePair implements GeneVariationPair {

    private final String geneName;
    private final SequenceVariation variation;

    public GeneFeaturePair(String feature) throws ParseException {

        String geneName = parseGeneName(feature);
        String variation = parseVariation(feature);
        SequenceVariationFormat parser = newParser();

        this.variation = parser.parse(variation);
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

    public static String getGeneName(String feature) {

        Preconditions.checkNotNull(feature);

        return feature.substring(0, feature.indexOf("-"));
    }

    protected String parseGeneName(String feature) {

        return getGeneName(feature);
    }

    protected String parseVariation(String feature) {

        int lastDashPosition = feature.lastIndexOf("-");
        return feature.substring(lastDashPosition + 1);
    }

    public String getGeneName() {
        return geneName;
    }

    @Override
    public SequenceVariation getVariation() {
        return variation;
    }

    protected abstract SequenceVariationFormat newParser();
}
