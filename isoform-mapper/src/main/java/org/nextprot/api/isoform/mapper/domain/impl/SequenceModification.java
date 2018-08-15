package org.nextprot.api.isoform.mapper.domain.impl;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariation;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariationFormatter;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariationParser;
import org.nextprot.api.commons.bio.variation.prot.impl.SequenceVariationImpl;
import org.nextprot.api.commons.bio.variation.prot.impl.format.SequencePtmBioEditorFormat;
import org.nextprot.api.commons.bio.variation.prot.impl.seqchange.UniProtPTM;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.BeanService;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.isoform.mapper.domain.FeatureQuery;
import org.nextprot.api.isoform.mapper.domain.FeatureQueryException;
import org.nextprot.api.isoform.mapper.domain.SingleFeatureQuery;
import org.nextprot.api.isoform.mapper.domain.impl.exception.PreIsoformParsingException;
import org.nextprot.api.isoform.mapper.service.SequenceFeatureValidator;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * A post translational modification on an isoform sequence
 */
public class SequenceModification extends SequenceFeatureBase {

    private static final SequencePtmBioEditorFormat PTM_FORMAT = new SequencePtmBioEditorFormat();

    public SequenceModification(String feature, BeanService beanService) throws ParseException, PreIsoformParsingException {

        super(feature, AnnotationCategory.GENERIC_PTM, PTM_FORMAT, beanService);
    }

    @Override
    protected int getDelimitingPositionBetweenIsoformAndVariation(String feature) {

        return feature.indexOf("+");
    }

    @Override
    protected SequenceVariationFormatter<String> getSequenceVariationFormatter() {

        return PTM_FORMAT;
    }

    @Override
    protected String formatSequenceIdPart(Isoform isoform) {
        return null;
    }

    @Override
    public Isoform parseIsoform(String sequenceIdPart) throws ParseException {

        Isoform isoform = getBeanService().getBean(IsoformService.class).getIsoformByNameOrCanonical(sequenceIdPart);

        if (isoform == null) {
            throw new ParseException(sequenceIdPart, 0);
        }

        return isoform;
    }

    @Override
    protected SequenceVariation parseVariation(SequenceVariationParser parser, String variationPart) throws ParseException {

        SequenceVariationImpl.StartBuilding builder = new SequenceVariationImpl.StartBuilding();

        return parser.parse(variationPart, builder.fromAAs(getIsoform().getSequence()));
    }

    @Override
    public SequenceModificationValidator newValidator(SingleFeatureQuery query) {

        return new SequenceModificationValidator(query);
    }

    public static class SequenceModificationValidator extends SequenceFeatureValidator<SequenceModification> {

        // see https://swissprot.isb-sib.ch/wiki/pages/viewpage.action?pageId=72192562
        private final Map<String, Rule> rules;

        public SequenceModificationValidator(SingleFeatureQuery query) {
            super(query);

            rules = new HashMap<>();

            rules.put("PTM-0528", new Rule(AminoAcidCode.ASPARAGINE, "N[^P][STC]"));
            rules.put("PTM-0250", new Rule(AminoAcidCode.ARGININE));
            rules.put("PTM-0251", new Rule(AminoAcidCode.CYSTEINE));
            rules.put("PTM-0252", new Rule(AminoAcidCode.HISTIDINE));
            rules.put("PTM-0253", new Rule(AminoAcidCode.SERINE));
            rules.put("PTM-0254", new Rule(AminoAcidCode.THREONINE));
            rules.put("PTM-0255", new Rule(AminoAcidCode.TYROSINE));
            //TODO: we could add all other PTM-ids given the target supplied by ProteinModificationService
        }

        @Override
        protected void postChecks(SequenceModification sequenceModification) throws FeatureQueryException {

            checkModificationSite(sequenceModification);
        }

        private void checkModificationSite(SequenceModification sequenceModification) throws NonMatchingRuleException {

            SequenceVariation variation = sequenceModification.getProteinVariation();

            UniProtPTM ptm = (UniProtPTM)variation.getSequenceChange();

            if (!rules.containsKey(ptm.getValue())) {

                throw new IllegalStateException("Internal error: no rule found for "+ptm.getValue());
            }

            Rule rule = rules.get(ptm.getValue());
            String aas = sequenceModification.getIsoform().getSequence();
            int aaIndex = variation.getVaryingSequence().getFirstAminoAcidPos() - 1;

            if (!rule.matches(aas, aaIndex)) {

                throw new NonMatchingRuleException(query, ptm, rule.getAminoAcidSite(aas, aaIndex));
            }
        }

        private static class Rule {

            private final AminoAcidCode modifiedAminoAcid;
            private final Pattern pattern;
            private final int window = 10;

            public Rule(AminoAcidCode modifiedAminoAcid) {

                this(modifiedAminoAcid, null);
            }

            public Rule(AminoAcidCode modifiedAminoAcid, String regionRegexp) {

                this.modifiedAminoAcid = modifiedAminoAcid;
                this.pattern = (regionRegexp != null) ? Pattern.compile("^"+regionRegexp+".*$") : null;
            }

            public boolean matches(String aas, int modifiedAminoAcidIndex) {

                if (modifiedAminoAcid.get1LetterCode().charAt(0) == aas.charAt(modifiedAminoAcidIndex)) {

                    if (pattern != null) {
                        return pattern.matcher(getAminoAcidSite(aas, modifiedAminoAcidIndex)).matches();
                    }
                    return true;
                }

                return false;
            }

            public String getAminoAcidSite(String aas, int modifiedAminoAcid) {

                return aas.substring(modifiedAminoAcid, modifiedAminoAcid + window);
            }
        }

        public static class NonMatchingRuleException extends FeatureQueryException {

            public NonMatchingRuleException(FeatureQuery query, UniProtPTM ptm, String aas) {

                super(query);

                getReason().addCause("PTM", ptm.getValue());
                getReason().setMessage("could not match PTM rule on aas " + aas);
            }
        }
    }
}
