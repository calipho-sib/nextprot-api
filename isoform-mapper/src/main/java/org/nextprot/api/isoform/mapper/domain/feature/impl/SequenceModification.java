package org.nextprot.api.isoform.mapper.domain.feature.impl;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.app.ApplicationContextProvider;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariation;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariationBuildException;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariationFormatter;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariationParser;
import org.nextprot.api.commons.bio.variation.prot.impl.SequenceVariationImpl;
import org.nextprot.api.commons.bio.variation.prot.impl.format.PtmBioEditorFormat;
import org.nextprot.api.commons.bio.variation.prot.impl.seqchange.UniProtPTM;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.isoform.mapper.domain.query.FeatureQuery;
import org.nextprot.api.isoform.mapper.domain.query.FeatureQueryException;
import org.nextprot.api.isoform.mapper.domain.query.SingleFeatureQuery;
import org.nextprot.api.isoform.mapper.domain.feature.SequenceFeatureValidator;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * A post translational modification on an isoform sequence
 */
public class SequenceModification extends SequenceFeatureBase {

    private static final PtmBioEditorFormat PTM_FORMAT = new PtmBioEditorFormat();

    public SequenceModification(String feature) throws ParseException, SequenceVariationBuildException {

        super(feature, AnnotationCategory.GENERIC_PTM, PTM_FORMAT);
    }

    @Override
    protected String getDelimitorBetweenIsoformAndVariation() {

        return ".";
    }

    @Override
    protected int getDelimitingPositionBetweenIsoformAndVariation(String feature) {

        return feature.indexOf(getDelimitorBetweenIsoformAndVariation());
    }

    @Override
    protected SequenceVariationFormatter<String> getSequenceVariationFormatter() {

        return PTM_FORMAT;
    }

    @Override
    protected String formatSequenceIdPart(Isoform isoform) {

        return isoform.getIsoformAccession();
    }

    @Override
    public Isoform parseIsoform(String sequenceIdPart) throws ParseException {

        Isoform isoform = ApplicationContextProvider.getApplicationContext().getBean(IsoformService.class)
                .getIsoformByNameOrCanonical(sequenceIdPart);

        if (isoform == null) {
            throw new ParseException(sequenceIdPart, 0);
        }

        return isoform;
    }

    @Override
    protected boolean isIsoformSpecific(String sequenceIdPart) {

        return sequenceIdPart.contains("-");
    }

    @Override
    protected SequenceVariation parseVariation(SequenceVariationParser parser, String variationPart) throws ParseException, SequenceVariationBuildException {

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

            rules.put("PTM-0528", new Rule("PTM-0528", AminoAcidCode.ASPARAGINE, "N[^P][STC]")); // TODO: this rule is not followed by NP1 (NX_Q14624.PTM-0528_274)
            rules.put("PTM-0529", new Rule("PTM-0529", AminoAcidCode.ASPARAGINE, "N[^P][STC]")); // TODO: this rule is not followed by NP1
            rules.put("PTM-0530", new Rule("PTM-0530", AminoAcidCode.ASPARAGINE, "N[^P][STC]")); // TODO: this rule is not followed by NP1
            rules.put("PTM-0531", new Rule("PTM-0531", AminoAcidCode.ASPARAGINE, "N[^P][STC]")); // TODO: this rule is not followed by NP1
            rules.put("PTM-0532", new Rule("PTM-0532", AminoAcidCode.ASPARAGINE, "N[^P][STC]")); // TODO: this rule is not followed by NP1
            rules.put("PTM-0568", new Rule("PTM-0568",AminoAcidCode.THREONINE));
            rules.put("PTM-0250", new Rule("PTM-0250",AminoAcidCode.ARGININE));
            rules.put("PTM-0251", new Rule("PTM-0251",AminoAcidCode.CYSTEINE));
            rules.put("PTM-0252", new Rule("PTM-0252",AminoAcidCode.HISTIDINE));
            rules.put("PTM-0253", new Rule("PTM-0253",AminoAcidCode.SERINE));
            rules.put("PTM-0254", new Rule("PTM-0254",AminoAcidCode.THREONINE));
            rules.put("PTM-0255", new Rule("PTM-0255",AminoAcidCode.TYROSINE));
            rules.put("PTM-0237", new Rule("PTM-0237",AminoAcidCode.ARGININE));
            rules.put("PTM-0565", new Rule("PTM-0565",AminoAcidCode.SERINE));
            rules.put("PTM-0550", new Rule("PTM-0550",AminoAcidCode.SERINE));
            rules.put("PTM-0551", new Rule("PTM-0551",AminoAcidCode.SERINE));
            rules.put("PTM-0552", new Rule("PTM-0552",AminoAcidCode.THREONINE));
            rules.put("PTM-0553", new Rule("PTM-0553",AminoAcidCode.THREONINE));
            rules.put("PTM-0574", new Rule("PTM-0574",AminoAcidCode.SERINE));
            rules.put("PTM-0580", new Rule("PTM-0580",AminoAcidCode.SERINE));
            rules.put("PTM-0582", new Rule("PTM-0582", AminoAcidCode.THREONINE));
            
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

                throw new NextProtException("PTM rule validation is missing: ptm=" + ptm.getValue() + ", feature="+sequenceModification.asString());
            }

            Rule rule = rules.get(ptm.getValue());
            String aas = sequenceModification.getIsoform().getSequence();
            int aaIndex = variation.getVaryingSequence().getFirstAminoAcidPos() - 1;

            if (!rule.matches(aas, aaIndex)) {

                throw new NonMatchingRuleException(query, ptm, rule, rule.getAminoAcidSite(aas, aaIndex));
            }
        }

        public static class Rule {

            private final String name;
            private final AminoAcidCode modifiedAminoAcid;
            private final Pattern pattern;
            private final int window = 10;

            public Rule(String name, AminoAcidCode modifiedAminoAcid) {

                this(name, modifiedAminoAcid, null);
            }

            public Rule(String name, AminoAcidCode modifiedAminoAcid, String regionRegexp) {

                this.name = name;
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

            public String getName() {
                return name;
            }

            public Pattern getPattern() {
                return pattern;
            }

            private String getAminoAcidSite(String aas, int modifiedAminoAcid) {

                Preconditions.checkElementIndex(modifiedAminoAcid, aas.length());

                int lastIndex = (modifiedAminoAcid + window < aas.length()) ? modifiedAminoAcid + window : aas.length();

                return aas.substring(modifiedAminoAcid, lastIndex);
            }

            @Override
            public String toString() {
                return "Rule{" +
                        "name='" + name + '\'' +
                        ", pattern=" + pattern +
                        '}';
            }
        }

        public static class NonMatchingRuleException extends FeatureQueryException {

            public NonMatchingRuleException(FeatureQuery query, UniProtPTM ptm, Rule rule, String aas) {

                super(query);

                getReason().addCause("PTM", ptm.getValue());
                getReason().setMessage("Could not match PTM rule "+ rule.getName() +": pattern="+rule.getPattern()+", target amino-acids=" + aas);
            }
        }
    }
}
