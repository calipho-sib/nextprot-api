package com.nextprot.api.isoform.mapper.domain.impl;

import com.google.common.base.Preconditions;
import com.nextprot.api.isoform.mapper.domain.SequenceFeature;
import com.nextprot.api.isoform.mapper.domain.impl.exception.UnknownIsoformException;
import com.nextprot.api.isoform.mapper.utils.EntryIsoformUtils;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.SequenceChange;
import org.nextprot.api.commons.bio.variation.SequenceVariation;
import org.nextprot.api.commons.bio.variation.SequenceVariationFormat;
import org.nextprot.api.core.dao.EntityName;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;

import java.text.ParseException;
import java.util.List;

/**
 * Parse and provide gene name and protein sequence variation
 */
public abstract class SequenceFeatureBase implements SequenceFeature {

    private final String geneName;
    private final String isoformName;
    private final String formattedVariation;
    private final SequenceVariation variation;
    private final SequenceVariationFormat parser;

    SequenceFeatureBase(String feature) throws ParseException {

        Preconditions.checkNotNull(feature);

        String variation = parseVariation(feature);
        parser = newParser();

        this.geneName = parseGeneName(feature);
        this.isoformName = parseIsoformName(feature);
        this.formattedVariation = variation;
        this.variation = parser.parse(variation);
    }

    protected abstract String formatIsoformFeatureName(Isoform isoform);

    /** Parse isoform name or null if not found */
    protected abstract String parseIsoformName(String feature) throws ParseException;

    protected abstract SequenceVariationFormat newParser();

    protected String parseVariation(String feature) {

        int lastDashPosition = feature.lastIndexOf("-");
        return feature.substring(lastDashPosition + 1);
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

    @Override
    public Isoform getIsoform(Entry entry) throws UnknownIsoformException {

        Isoform isoform = (isoformName != null) ?
                EntryIsoformUtils.getIsoformByName(entry, isoformName) :
                EntryIsoformUtils.getCanonicalIsoform(entry);

        if (isoform == null) {
            throw new UnknownIsoformException(isoformName, entry);
        }

        return isoform;
    }

    @Override
    public boolean isValidIsoform(Entry entry) {

        Isoform isoform = (isoformName != null) ?
                EntryIsoformUtils.getIsoformByName(entry, isoformName) :
                EntryIsoformUtils.getCanonicalIsoform(entry);

        return isoform != null;
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
    public String formatIsoSpecificFeature(Isoform isoform, int firstPos, int lastPos) {

        // create a new variation specific to the isoform
        SequenceVariationSimple isoVariation = new SequenceVariationSimple();
        isoVariation.setFirst(variation.getFirstChangingAminoAcid());
        isoVariation.setLast(variation.getLastChangingAminoAcid());
        isoVariation.setFirstPos(firstPos);
        isoVariation.setLastPos(lastPos);
        isoVariation.setChange(variation.getSequenceChange());

        StringBuilder sb = new StringBuilder()
                .append(geneName)
                .append("-")
                .append(formatIsoformFeatureName(isoform))
                .append("-")
                .append(parser.format(isoVariation, AminoAcidCode.AACodeType.THREE_LETTER));

        return sb.toString();
    }


    private String parseGeneName(String feature) {

        return getGeneName(feature);
    }

    @Override
    public String getGeneName() {
        return geneName;
    }

    @Override
    public String getIsoformName() {
        return isoformName;
    }

    @Override
    public SequenceVariation getProteinVariation() {
        return variation;
    }

    public static class SequenceVariationSimple implements SequenceVariation {

        private AminoAcidCode first, last;
        private int firstPos, lastPos;
        private SequenceChange change;

        public void setFirst(AminoAcidCode first) {
            this.first = first;
        }

        public void setLast(AminoAcidCode last) {
            this.last = last;
        }

        public void setFirstPos(int firstPos) {
            this.firstPos = firstPos;
        }

        public void setLastPos(int lastPos) {
            this.lastPos = lastPos;
        }

        public void setChange(SequenceChange change) {
            this.change = change;
        }

        @Override
        public AminoAcidCode getFirstChangingAminoAcid() {
            return first;
        }

        @Override
        public int getFirstChangingAminoAcidPos() {
            return firstPos;
        }

        @Override
        public AminoAcidCode getLastChangingAminoAcid() {
            return last;
        }

        @Override
        public int getLastChangingAminoAcidPos() {
            return lastPos;
        }

        @Override
        public boolean isMultipleChangingAminoAcids() {
            return lastPos-firstPos > 0;
        }

        @Override
        public SequenceChange getSequenceChange() {
            return change;
        }
    }

}
