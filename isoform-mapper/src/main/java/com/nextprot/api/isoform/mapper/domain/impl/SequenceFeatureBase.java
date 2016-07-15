package com.nextprot.api.isoform.mapper.domain.impl;

import com.google.common.base.Preconditions;
import com.nextprot.api.isoform.mapper.domain.SequenceFeature;
import org.nextprot.api.commons.bio.variation.SequenceVariation;
import org.nextprot.api.commons.bio.variation.SequenceVariationFormat;
import org.nextprot.api.core.dao.EntityName;
import org.nextprot.api.core.domain.Entry;

import java.text.ParseException;
import java.util.List;

/**
 * Parse and provide gene name and protein sequence variation
 */
public abstract class SequenceFeatureBase implements SequenceFeature {

    private final String geneName;
    private final String formattedVariation;
    private final SequenceVariation variation;

    public SequenceFeatureBase(String feature) throws ParseException {

        String geneName = parseGeneName(feature);
        String variation = parseVariation(feature);
        SequenceVariationFormat parser = newParser();

        this.geneName = geneName;
        this.formattedVariation = variation;
        this.variation = parser.parse(variation);
    }

    @Override
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

    @Override
    public String getFormattedVariation() {

        return formattedVariation;
    }

    @Override
    public String formatIsoSpecificFeature(int isoNumber) {

        Preconditions.checkArgument(isoNumber>0, isoNumber + ": isoform number should be a positive number");

        StringBuilder sb = new StringBuilder();

        sb
                .append(geneName)
                .append("-iso").append(isoNumber).append("-")
                .append(formattedVariation);

        return sb.toString();
    }

    private String parseGeneName(String feature) {

        return getGeneName(feature);
    }

    protected String parseVariation(String feature) {

        int lastDashPosition = feature.lastIndexOf("-");
        return feature.substring(lastDashPosition + 1);
    }

    @Override
    public String getGeneName() {
        return geneName;
    }

    @Override
    public SequenceVariation getVariation() {
        return variation;
    }

    protected abstract SequenceVariationFormat newParser();
}
