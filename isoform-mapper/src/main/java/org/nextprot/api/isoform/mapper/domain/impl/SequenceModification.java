package org.nextprot.api.isoform.mapper.domain.impl;

import org.nextprot.api.commons.bio.variation.prot.SequenceVariation;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariationFormatter;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariationParser;
import org.nextprot.api.commons.bio.variation.prot.impl.SequenceVariationImpl;
import org.nextprot.api.commons.bio.variation.prot.impl.format.SequencePtmBioEditorFormat;
import org.nextprot.api.commons.bio.variation.prot.impl.seqchange.PTM;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.BeanService;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.isoform.mapper.domain.FeatureQuery;
import org.nextprot.api.isoform.mapper.domain.FeatureQueryException;
import org.nextprot.api.isoform.mapper.domain.SingleFeatureQuery;
import org.nextprot.api.isoform.mapper.domain.impl.exception.PreIsoformParsingException;
import org.nextprot.api.isoform.mapper.service.SequenceFeatureValidator;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
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

        Entry entry = getBeanService().getBean(EntryBuilderService.class).build(EntryConfig.newConfig(query.getAccession())
                .withTargetIsoforms().withOverview());

        return new SequenceModificationValidator(entry, query);
    }

    public static class SequenceModificationValidator extends SequenceFeatureValidator<SequenceModification> {

        // see https://swissprot.isb-sib.ch/wiki/pages/viewpage.action?pageId=72192562
        private final Map<String, Rule> rules;

        public SequenceModificationValidator(Entry entry, SingleFeatureQuery query) {
            super(entry, query);

            rules = new HashMap<>();

            rules.put("PTM-0528", new Rule("N[^P][STC]"));
            rules.put("PTM-0250", new Rule("R"));
            rules.put("PTM-0251", new Rule("C"));
            rules.put("PTM-0252", new Rule("H"));
            rules.put("PTM-0253", new Rule("S"));
            rules.put("PTM-0254", new Rule("T"));
            rules.put("PTM-0255", new Rule("Y"));
        }

        @Override
        protected void postChecks(SequenceModification sequenceModification) throws FeatureQueryException {

            checkModificationSite(sequenceModification);
        }

        private void checkModificationSite(SequenceModification sequenceModification) throws NonMatchingRuleException {

            SequenceVariation variation = sequenceModification.getProteinVariation();

            PTM ptm = (PTM)variation.getSequenceChange();

            if (!rules.containsKey(ptm.getValue())) {

                throw new IllegalStateException("Internal error: no rule found for "+ptm.getValue());
            }

            Rule rule = rules.get(ptm.getValue());
            if (!rule.apply(sequenceModification.getIsoform().getSequence(),
                    variation.getVaryingSequence().getFirstAminoAcidPos() - 1)) {

                throw new NonMatchingRuleException(query, ptm, rule.getAminoAcidSite(sequenceModification.getIsoform().getSequence(),
                        variation.getVaryingSequence().getFirstAminoAcidPos() - 1));
            }
        }

        private static class Rule {

            private final Pattern pattern;
            private final int window = 10;

            public Rule(String regexp) {

                this.pattern = Pattern.compile("^"+regexp+".*$");
            }

            public boolean apply(String aas, int modifiedAminoAcid) {

                Matcher matcher = pattern.matcher(getAminoAcidSite(aas, modifiedAminoAcid));

                return matcher.matches();
            }

            public String getAminoAcidSite(String aas, int modifiedAminoAcid) {

                return aas.substring(modifiedAminoAcid, modifiedAminoAcid + window);
            }
        }

        public static class NonMatchingRuleException extends FeatureQueryException {

            public NonMatchingRuleException(FeatureQuery query, PTM ptm, String aas) {

                super(query);

                getReason().addCause("PTM", ptm.getValue());
                getReason().setMessage("could not match PTM rule on aas " + aas);
            }
        }
    }
}
