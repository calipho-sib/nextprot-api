package org.nextprot.api.isoform.mapper.domain.feature.impl;

import org.nextprot.api.commons.app.ApplicationContextProvider;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariationBuildException;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariationFormatter;
import org.nextprot.api.commons.bio.variation.prot.impl.format.VariantHGVSFormat;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.EntityName;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.Overview;
import org.nextprot.api.core.service.EntityNameService;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.utils.IsoformUtils;
import org.nextprot.api.isoform.mapper.domain.query.FeatureQueryException;
import org.nextprot.api.isoform.mapper.domain.query.SingleFeatureQuery;
import org.nextprot.api.isoform.mapper.domain.impl.exception.IncompatibleGeneAndProteinNameException;
import org.nextprot.api.isoform.mapper.domain.impl.exception.PreIsoformParseException;
import org.nextprot.api.isoform.mapper.domain.impl.exception.UnknownGeneNameException;
import org.nextprot.api.isoform.mapper.domain.feature.SequenceFeatureValidator;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SequenceVariant extends SequenceFeatureBase {

    private static final VariantHGVSFormat HGVS_FORMAT = new VariantHGVSFormat();

    private String geneName;
    private String entryAccession;

    private SequenceVariant(String feature, AnnotationCategory type) throws ParseException, SequenceVariationBuildException {

        super(feature, type, HGVS_FORMAT);
    }

    public static SequenceVariant variant(String feature) throws ParseException, SequenceVariationBuildException {

        return new SequenceVariant(feature, AnnotationCategory.VARIANT);
    }

    public static SequenceVariant mutagenesis(String feature) throws ParseException, SequenceVariationBuildException {

        return new SequenceVariant(feature, AnnotationCategory.MUTAGENESIS);
    }

    @Override
    protected void preIsoformParsing(String sequenceIdPart) throws PreIsoformParseException {

        this.geneName = parseGeneName(sequenceIdPart);
        this.entryAccession = findEntryAccessionFromGeneName();
    }

    private String parseGeneName(String sequenceIdPart) {

        if (sequenceIdPart.contains("-"))
            return sequenceIdPart.substring(0, sequenceIdPart.indexOf("-"));

        return sequenceIdPart;
    }

    private String findEntryAccessionFromGeneName() throws UnknownGeneNameException {

        Set<String> entries = ApplicationContextProvider.getApplicationContext().getBean(MasterIdentifierService.class)
		        .findEntryAccessionByGeneName(geneName, false);

        if (entries == null || entries.isEmpty()) {
            throw new UnknownGeneNameException(geneName);
        }

        return entries.iterator().next();
    }

    @Override
    protected String getDelimitorBetweenIsoformAndVariation() {

        return "-";
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
     *   @return empty if implicit (canonical)
     */
    private Optional<String> extractExplicitIsoformName(String sequenceIdPart) throws PreIsoformParseException {

        if (!sequenceIdPart.contains("-")) {
            return Optional.empty();
        }

        int indexOfDash = sequenceIdPart.indexOf("-");

        if (indexOfDash >= 0) {
            String featureIsoname = sequenceIdPart.substring(indexOfDash + 1);

            if (featureIsoname.startsWith("iso")) {

                String name = featureIsoname.substring(3);
                if (name.matches("\\d+")) {
                    return Optional.of("Iso " + name);
                } else {
                    // replace back space from underscore: some isoform names contain spaces that were replaced by underscore
                    // see also method formatIsoformFeatureName()
                    name = name.replace("_", " ");

                    // Delta6 -> Delta 6
                    Pattern pat = Pattern.compile("(\\s+)(\\d+)");
                    Matcher matcher = pat.matcher(name);

                    if (matcher.find()) {

                        return Optional.of(matcher.group(1) + " " + matcher.group(2));
                    }

                    return Optional.of(name);
                }
            }

            throw new PreIsoformParseException("invalid isoform name: " + featureIsoname + " (isoform name should starts with prefix 'iso')");
        }

        return Optional.empty();
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

        Optional<String> isoSpecificName = extractExplicitIsoformName(sequenceIdPart);

        IsoformService isoformService = ApplicationContextProvider.getApplicationContext().getBean(IsoformService.class);

        // 2. get isoform accession from iso name and entry
        Isoform isoform = (isoSpecificName.isPresent()) ?
                isoformService.findIsoformByName(entryAccession, isoSpecificName.get()) :
                IsoformUtils.getCanonicalIsoform(ApplicationContextProvider.getApplicationContext().getBean(EntryBuilderService.class)
                        .build(EntryConfig.newConfig(entryAccession).withTargetIsoforms()));

        if (isoform == null) {
            throw new ParseException("Cannot find isoform from sequence part "+ sequenceIdPart+" (entry accession="+entryAccession+")", 0);
        }
        return isoform;
    }

    @Override
    protected boolean isIsoformSpecific(String sequenceIdPart) {

        return sequenceIdPart.contains("-iso");
    }

    @Override
    public SequenceVariantValidator newValidator(SingleFeatureQuery query) {

        List<EntityName> geneNames = ApplicationContextProvider.getApplicationContext().getBean(EntityNameService.class)
                .findNamesByEntityNameClass(query.getAccession(), Overview.EntityNameClass.GENE_NAMES);

        if (geneNames.isEmpty()) {
            throw new NextProtException("missing gene names for entry accession "+ query.getAccession());
        }

        return new SequenceVariantValidator(geneNames, query);
    }

    public static boolean isValidGeneName(List<EntityName> geneNames, String geneName) {

        if (geneName != null) {

            for (EntityName name : geneNames) {

                if (geneName.startsWith(name.getName())) {
                    return true;
                }
            }
        }

        return false;
    }

    public static class SequenceVariantValidator extends SequenceFeatureValidator<SequenceVariant> {

        private final List<EntityName> geneNames;

        public SequenceVariantValidator(List<EntityName> geneNames, SingleFeatureQuery query) {
            super(query);

            this.geneNames = geneNames;
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

            if (!SequenceVariant.isValidGeneName(geneNames, sequenceFeature.getGeneName())) {

                throw new IncompatibleGeneAndProteinNameException(query, sequenceFeature.getGeneName(),
                        geneNames.stream().map(EntityName::getName).collect(Collectors.toList()));
            }
        }
    }
}