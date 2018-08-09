package org.nextprot.api.isoform.mapper.domain.impl;

import org.nextprot.api.commons.bio.variation.prot.impl.format.SequenceGlycosylationBedFormat;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.BeanService;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.isoform.mapper.domain.FeatureQueryException;
import org.nextprot.api.isoform.mapper.domain.SingleFeatureQuery;
import org.nextprot.api.isoform.mapper.domain.impl.exception.UnknownIsoformException;
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
    public Isoform getIsoform() throws UnknownIsoformException {

        return beanService.getBean(IsoformService.class).getIsoformByNameOrCanonical(sequenceIdPart);
    }

    @Override
    public SequenceModificationValidator newValidator(Entry entry, SingleFeatureQuery query) {

        return new SequenceModificationValidator(entry, query);
    }

    public static class SequenceModificationValidator extends SequenceFeatureValidator<SequenceModification> {

        public SequenceModificationValidator(Entry entry, SingleFeatureQuery query) {
            super(entry, query);
        }

        @Override
        protected void preChecks(SequenceModification sequenceModification) throws FeatureQueryException {

        }
    }
}
