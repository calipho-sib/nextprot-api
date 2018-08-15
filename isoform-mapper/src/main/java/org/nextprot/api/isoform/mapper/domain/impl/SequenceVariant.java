package org.nextprot.api.isoform.mapper.domain.impl;

import org.nextprot.api.commons.bio.variation.prot.SequenceVariationFormatter;
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
import org.nextprot.api.isoform.mapper.domain.impl.exception.PreIsoformParsingException;
import org.nextprot.api.isoform.mapper.domain.impl.exception.UnknownGeneNameException;
import org.nextprot.api.isoform.mapper.service.SequenceFeatureValidator;

import java.text.ParseException;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SequenceVariant extends SequenceFeatureBase {

    private static final SequenceVariantHGVSFormat HGVS_FORMAT = new SequenceVariantHGVSFormat();

    private String geneName;
    private String entryAccession;
    private String isoformName;

    private SequenceVariant(String feature, AnnotationCategory type, BeanService beanService) throws ParseException, PreIsoformParsingException {

        super(feature, type, HGVS_FORMAT, beanService);
    }

    public static SequenceVariant variant(String feature, BeanService beanService) throws ParseException, PreIsoformParsingException {

        return new SequenceVariant(feature, AnnotationCategory.VARIANT, beanService);
    }

    public static SequenceVariant mutagenesis(String feature, BeanService beanService) throws ParseException, PreIsoformParsingException {

        return new SequenceVariant(feature, AnnotationCategory.MUTAGENESIS, beanService);
    }

    @Override
    protected void preIsoformParsing(String sequenceIdPart) throws PreIsoformParsingException {

        this.geneName = parseGeneName(sequenceIdPart);
        this.entryAccession = findEntryAccessionFromGeneName();
        this.isoformName = extractIsoformName(sequenceIdPart);
    }

    private String parseGeneName(String sequenceIdPart) {

        if (sequenceIdPart.contains("-"))
            return sequenceIdPart.substring(0, sequenceIdPart.indexOf("-"));

        return sequenceIdPart;
    }

    private String findEntryAccessionFromGeneName() throws UnknownGeneNameException {

        Set<String> entries = getBeanService().getBean(MasterIdentifierService.class).findEntryAccessionByGeneName(geneName, false);

        if (entries == null || entries.isEmpty()) {
            throw new UnknownGeneNameException(geneName);
        }

        return entries.iterator().next();
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
    protected SequenceVariationFormatter<String> getSequenceVariationFormatter() {

        return HGVS_FORMAT;
    }


    /**
     *   isoshort  -> Short
     *   isolong   -> Long
     *   iso5      -> Iso 5
     *   isodelta6 -> Delta 6
     *
     *   @return null if canonical
     */
    private String extractIsoformName(String sequenceIdPart) throws PreIsoformParsingException {

        String featureIsoname = parseIsoformName(sequenceIdPart);

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

        throw new PreIsoformParsingException("invalid isoform name: "+featureIsoname+" (isoform name should starts with prefix 'iso')");
    }

    /**
     * @return the isoform part from feature string (null if canonical)
     */
    private String parseIsoformName(String sequenceIdPart) {

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

    public String getGeneName() {
        return geneName;
    }

    @Override
    protected Isoform parseIsoform(String sequenceIdPart) throws ParseException {

        BeanService beanService = getBeanService();
        IsoformService isoformService = beanService.getBean(IsoformService.class);

        // 2. get isoform accession from iso name and entry
        Isoform isoform = (isoformName != null) ? isoformService.findIsoformByName(entryAccession, isoformName) : IsoformUtils.getCanonicalIsoform(beanService.getBean(EntryBuilderService.class)
                .build(EntryConfig.newConfig(entryAccession).withTargetIsoforms()));

        if (isoform == null) {
            throw new ParseException("Cannot find isoform "+ isoformName+" from entry accession "+entryAccession, 0);
        }
        return isoform;
    }

    @Override
    public SequenceVariantValidator newValidator(SingleFeatureQuery query) {

        Entry entry = getBeanService().getBean(EntryBuilderService.class).build(EntryConfig.newConfig(query.getAccession())
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

    public static class SequenceVariantValidator extends SequenceFeatureValidator<SequenceVariant> {

        private final Entry entry;

        public SequenceVariantValidator(Entry entry, SingleFeatureQuery query) {
            super(query);

            this.entry = entry;
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