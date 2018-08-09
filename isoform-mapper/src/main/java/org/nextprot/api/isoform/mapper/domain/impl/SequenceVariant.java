package org.nextprot.api.isoform.mapper.domain.impl;

import org.nextprot.api.commons.bio.variation.prot.SequenceVariationFormat;
import org.nextprot.api.commons.bio.variation.prot.impl.format.SequenceVariantHGVSFormat;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.EntityName;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.BeanService;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.utils.IsoformUtils;
import org.nextprot.api.isoform.mapper.domain.FeatureQueryException;
import org.nextprot.api.isoform.mapper.domain.SingleFeatureQuery;
import org.nextprot.api.isoform.mapper.domain.impl.exception.IncompatibleGeneAndProteinNameException;
import org.nextprot.api.isoform.mapper.domain.impl.exception.UnknownIsoformException;
import org.nextprot.api.isoform.mapper.service.SequenceFeatureValidator;

import java.text.ParseException;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SequenceVariant extends SequenceFeatureBase {

    private final String geneName;
    private final String isoformName;

    private SequenceVariant(String feature, AnnotationCategory type, BeanService beanService) throws ParseException {
        super(feature, type, beanService);

        this.geneName = parseGeneName();
        this.isoformName = extractIsoformName();
    }

    public static SequenceVariant variant(String feature, BeanService beanService) throws ParseException {

        return new SequenceVariant(feature, AnnotationCategory.VARIANT, beanService);
    }

    public static SequenceVariant mutagenesis(String feature, BeanService beanService) throws ParseException {

        return new SequenceVariant(feature, AnnotationCategory.MUTAGENESIS, beanService);
    }

    private String parseGeneName() {

        if (sequenceIdPart.contains("-"))
            return sequenceIdPart.substring(0, sequenceIdPart.indexOf("-"));

        return sequenceIdPart;
    }

    @Override
    protected int getDelimitingPositionBetweenIsoformAndVariation(String feature) throws ParseException {

        int index = feature.indexOf("-p.");

        if (index >= 0) {
            return index;
        }
        throw new ParseException("Cannot separate gene name from variation (missing '-p.')", 0);
    }

    @Override
    public SequenceVariationFormat newParser() {

        return new SequenceVariantHGVSFormat();
    }

    /**
     *
     *   isoshort  -> Short
     *   isolong   -> Long
     *   iso5      -> Iso 5
     *   isodelta6 -> Delta 6
     *
     *   @return null if canonical
     */
    private String extractIsoformName() throws ParseException {

        String featureIsoname = parseIsoformName();

        // canonical
        if (featureIsoname == null) {
            return null;
        }
        else if (featureIsoname.startsWith("iso")) {

            String name = featureIsoname.substring(3);
            if (name.matches("\\d+")) {
                return "Iso "+name;
            }
            else {
                // replace back space from underscore: some isoform names contain spaces that were replaced by underscore
                // see also method formatIsoformFeatureName()
                name = name.replace("_", " ");

                // Delta6 -> Delta 6
                Pattern pat = Pattern.compile("(\\s+)(\\d+)");
                Matcher matcher = pat.matcher(name);

                if (matcher.find()) {

                    return matcher.group(1)+" "+matcher.group(2);
                }
                return name;
            }
        }

        throw new ParseException("invalid isoform name: "+featureIsoname+" (isoform name should starts with prefix 'iso')", 0);
    }

    /**
     * @return the isoform part from feature string (null if canonical)
     */
    private String parseIsoformName() {

        int indexOfDash = sequenceIdPart.indexOf("-");

        if (indexOfDash >= 0) {
            return sequenceIdPart.substring(indexOfDash+1);
        }

        return null;
    }

    @Override
    protected String formatSequenceIdPart(Isoform isoform) {

        StringBuilder sb = new StringBuilder(geneName);
        sb.append("-");
        sb.append(formatIsoformName(isoform));

        return sb.toString();
    }

    String formatIsoformName(Isoform isoform) {

        String name = isoform.getMainEntityName().getName();
        StringBuilder sb = new StringBuilder();

        if (name.startsWith("Iso"))
            sb.append(name.toLowerCase().replace(" ", ""));
        else
            sb.append("iso").append(name.replace(" ", "_"));

        return sb.toString();
    }

    public String getIsoformName() {
        return isoformName;
    }

    public String getGeneName() {
        return geneName;
    }


    // TODO: this method should be called after isoformName has been set by getIsoformName(seqId)
    @Override
    public Isoform buildIsoform() throws UnknownGeneNameException {

        Set<String> entries = beanService.getBean(MasterIdentifierService.class).findEntryAccessionByGeneName(geneName, false);

        if (entries != null && !entries.isEmpty()) {

            IsoformService isoformService = beanService.getBean(IsoformService.class);

            // 1. get entry from gene name
            String entry = entries.iterator().next();

            // 2. get isoform accession from iso name and entry
            return (isoformName != null) ? isoformService.findIsoformByName(entry, isoformName) : IsoformUtils.getCanonicalIsoform(beanService.getBean(EntryBuilderService.class)
                    .build(EntryConfig.newConfig(entry).withTargetIsoforms()));
        }

        throw new UnknownGeneNameException(geneName);
    }

    @Override
    public SequenceVariantValidator newValidator(SingleFeatureQuery query) {

        Entry entry = beanService.getBean(EntryBuilderService.class).build(EntryConfig.newConfig(query.getAccession())
                .withTargetIsoforms().withOverview());

        return new SequenceVariantValidator(entry, query);
    }

    public static boolean isValidGeneName(Entry entry, String geneName) {

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

    public class UnknownGeneNameException extends UnknownIsoformException {

        private final String geneName;

        public UnknownGeneNameException(String geneName) {

            super("Cannot find a neXtProt entry associated with gene name "+ geneName);
            this.geneName = geneName;
        }

        public String getGeneName() {
            return geneName;
        }
    }

    public static class SequenceVariantValidator extends SequenceFeatureValidator<SequenceVariant> {

        public SequenceVariantValidator(Entry entry, SingleFeatureQuery query) {
            super(entry, query);
        }

        @Override
        protected void preChecks(SequenceVariant sequenceVariant) throws FeatureQueryException {

            checkFeatureGeneName(sequenceVariant);
        }

        /**
         * Check that gene name is compatible with protein name
         * Part of the contract a validator should implement to validate a feature on an isoform sequence
         */
        private void checkFeatureGeneName(SequenceVariant sequenceFeature) throws IncompatibleGeneAndProteinNameException {

            if (!SequenceVariant.isValidGeneName(entry, sequenceFeature.getGeneName())) {

                throw new IncompatibleGeneAndProteinNameException(query, sequenceFeature.getGeneName(),
                        entry.getOverview().getGeneNames().stream().map(EntityName::getName).collect(Collectors.toList()));
            }
        }
    }
}