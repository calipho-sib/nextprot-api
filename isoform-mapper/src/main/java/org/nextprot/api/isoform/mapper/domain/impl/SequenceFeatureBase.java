package org.nextprot.api.isoform.mapper.domain.impl;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.prot.varseq.VaryingSequence;
import org.nextprot.api.commons.bio.variation.prot.seqchange.SequenceChange;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariation;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariationFormat;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.dao.EntityName;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.utils.IsoformUtils;
import org.nextprot.api.isoform.mapper.domain.FeatureQueryException;
import org.nextprot.api.isoform.mapper.domain.SequenceFeature;
import org.nextprot.api.isoform.mapper.domain.SingleFeatureQuery;
import org.nextprot.api.isoform.mapper.domain.impl.exception.InvalidFeatureQueryFormatException;
import org.nextprot.api.isoform.mapper.domain.impl.exception.InvalidFeatureQueryTypeException;
import org.nextprot.api.isoform.mapper.domain.impl.exception.UnknownIsoformRuntimeException;

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

        feature = feature.trim();

        int pivotPoint = getPivotPoint(feature);

        String genePlusIso = feature.substring(0, pivotPoint);
        formattedVariation = feature.substring(pivotPoint+1);

        geneName = parseGeneName(genePlusIso);
        isoformName = parseIsoformName(genePlusIso);

        parser = newParser();
        variation = parser.parse(formattedVariation);
    }

    protected abstract int getPivotPoint(String feature) throws ParseException;

    public static SequenceFeature newFeature(SingleFeatureQuery query) throws FeatureQueryException {

        // throw exception if invalid query
        query.checkFeatureQuery();

        AnnotationCategory annotationCategory = AnnotationCategory.getDecamelizedAnnotationTypeName(query.getFeatureType());

        try {
            switch (annotationCategory) {
                case MUTAGENESIS:
                case VARIANT:
                    return new SequenceVariant(query.getFeature());
                case GENERIC_PTM:
                    return new SequenceModification(query.getFeature());
                default:
                    throw new InvalidFeatureQueryTypeException(query);
            }
        }
        catch (ParseException e) {
            throw new InvalidFeatureQueryFormatException(query, e);
        }
    }

    protected abstract String formatIsoformFeatureName(Isoform isoform);

    /** Parse isoform name or null if not found */
    protected abstract String parseIsoformName(String feature) throws ParseException;

    protected abstract SequenceVariationFormat newParser();

    @Override
    public boolean isValidGeneName(Entry entry) {

        if (geneName != null) {

            List<EntityName> geneNames = entry.getOverview().getGeneNames();

            for (EntityName name : geneNames) {

                if (geneName.startsWith(name.getName())) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public Isoform getIsoform(Entry entry) throws UnknownIsoformRuntimeException {

        Isoform isoform = (isoformName != null) ?
                IsoformUtils.getIsoformByName(entry, isoformName) :
                IsoformUtils.getCanonicalIsoform(entry);

        if (isoform == null) {
            throw new UnknownIsoformRuntimeException(isoformName, entry);
        }

        return isoform;
    }

    @Override
    public boolean isValidIsoform(Entry entry) {

        Isoform isoform = (isoformName != null) ?
                IsoformUtils.getIsoformByName(entry, isoformName) :
                IsoformUtils.getCanonicalIsoform(entry);

        return isoform != null;
    }

    @Override
    public String getFormattedVariation() {

        return formattedVariation;
    }

    @Override
    public String formatIsoSpecificFeature(Isoform isoform, int firstPos, int lastPos) {

        // create a new variation specific to the isoform
        SequenceVariationSimple isoVariation = new SequenceVariationSimple();

        VaryingSequenceSimple changingSequence = new VaryingSequenceSimple();

        changingSequence.setFirst(variation.getVaryingSequence().getFirstAminoAcid());
        changingSequence.setLast(variation.getVaryingSequence().getLastAminoAcid());
        changingSequence.setFirstPos(firstPos);
        changingSequence.setLastPos(lastPos);

        isoVariation.setVaryingSequence(changingSequence);
        isoVariation.setChange(variation.getSequenceChange());

        StringBuilder sb = new StringBuilder()
                .append(geneName)
                .append("-")
                .append(formatIsoformFeatureName(isoform))
                .append("-")
                .append(parser.format(isoVariation, AminoAcidCode.CodeType.THREE_LETTER));

        return sb.toString();
    }

    private String parseGeneName(String geneAndIso) {

        Preconditions.checkNotNull(geneAndIso);

        if (geneAndIso.contains("-"))
            return geneAndIso.substring(0, geneAndIso.indexOf("-"));

        return geneAndIso;
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

        private VaryingSequence varyingSequence;
        private SequenceChange change;

        public void setChange(SequenceChange change) {
            this.change = change;
        }

        public void setVaryingSequence(VaryingSequence varyingSequence) {
            this.varyingSequence = varyingSequence;
        }

        @Override
        public VaryingSequence getVaryingSequence() {
            return varyingSequence;
        }

        @Override
        public SequenceChange getSequenceChange() {
            return change;
        }
    }

    public static class VaryingSequenceSimple implements VaryingSequence {

        private AminoAcidCode first, last;
        private int firstPos, lastPos;

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

        @Override
        public AminoAcidCode getFirstAminoAcid() {
            return first;
        }

        @Override
        public int getFirstAminoAcidPos() {
            return firstPos;
        }

        @Override
        public AminoAcidCode getLastAminoAcid() {
            return last;
        }

        @Override
        public int getLastAminoAcidPos() {
            return lastPos;
        }
    }
}
