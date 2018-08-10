package org.nextprot.api.isoform.mapper.domain.impl;

import org.nextprot.api.commons.bio.variation.prot.impl.format.SequenceGlycosylationBedFormat;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.BeanService;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.isoform.mapper.domain.FeatureQueryException;
import org.nextprot.api.isoform.mapper.domain.SingleFeatureQuery;
import org.nextprot.api.isoform.mapper.service.SequenceFeatureValidator;

import java.text.ParseException;

/**
 * A post translational modification on an isoform sequence
 */
public class SequenceModification extends SequenceFeatureBase {

    public SequenceModification(String feature, BeanService beanService) throws ParseException {

        super(feature, AnnotationCategory.GENERIC_PTM, beanService);
    }

    @Override
    protected int getDelimitingPositionBetweenIsoformAndVariation(String feature) {

        return feature.indexOf("+");
    }

    @Override
    public SequenceGlycosylationBedFormat newParser() {

        return new SequenceGlycosylationBedFormat();
    }

    @Override
    protected String formatSequenceIdPart(Isoform isoform) {
        return null;
    }

    @Override
    public Isoform buildIsoform() {

        return beanService.getBean(IsoformService.class).getIsoformByNameOrCanonical(sequenceIdPart);
    }

    @Override
    public SequenceModificationValidator newValidator(SingleFeatureQuery query) {

        Entry entry = beanService.getBean(EntryBuilderService.class).build(EntryConfig.newConfig(query.getAccession())
                .withTargetIsoforms().withOverview());

        return new SequenceModificationValidator(entry, query);
    }

    public static class SequenceModificationValidator extends SequenceFeatureValidator<SequenceModification> {

        public SequenceModificationValidator(Entry entry, SingleFeatureQuery query) {
            super(entry, query);
        }

        @Override
        protected void postChecks(SequenceModification sequenceModification) throws FeatureQueryException {

            checkModificationSite(sequenceModification);
        }

        /*
        According to the ptm, we check here that the site is ptmable through a residue regular expression.

        Example (N-Glycosylation): (see https://swissprot.isb-sib.ch/wiki/pages/viewpage.action?pageId=72192562)
            Residue pattern:
            N-{P}-[STC]-{P} -> GOLD
            N-{P}-[STC]  -> SILVER, unless from Swiss-Prot -> GOLD

        [Add keyword Glycoprotein [KW-0325] if needed]
         */
        private void checkModificationSite(SequenceModification sequenceModification) {

            /*
            String aas = sequenceModification.buildIsoform().getSequence();

            int site = sequenceModification.getProteinVariation().getVaryingSequence().getFirstAminoAcidPos()-1;

            if (aas.charAt(site+1) == 'P') {
                throw new IllegalStateException("P should not be found at pos "+(site+1));
            }

            if (aas.charAt(site+2) != 'S' && aas.charAt(site+2) != 'T' && aas.charAt(site+2) != 'C') {
                throw new IllegalStateException("Missing ST or C at pos "+(site+2));
            }

            throw new UnexpectedFeatureQueryAminoAcidException(query, site,
                    AminoAcidCode.valueOfAminoAcidCodeSequence(aasOnSequence),
                    AminoAcidCode.valueOfAminoAcidCodeSequence(aas));

            System.out.println(sequenceModification);

            // ptmid -> rule
            */
        }
    }
}
