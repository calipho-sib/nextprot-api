package org.nextprot.api.isoform.mapper.domain;

import org.nextprot.api.commons.bio.variation.prot.SequenceVariation;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.isoform.mapper.domain.impl.exception.UnknownIsoformException;
import org.nextprot.api.isoform.mapper.service.SequenceFeatureValidator;

/**
 * A sequence feature on an isoform sequence on a specific gene
 */
public interface SequenceFeature {

    /**
     * Format a feature specifically to isoform
     * @param isoform the specific isoform
     * @param firstAAPos first position of the feature
     * @param lastAAPos last position of the feature
     * @return a format of the feature on the isoform
     */
    String formatIsoSpecificFeature(Isoform isoform, int firstAAPos, int lastAAPos);

    /** @return the protein sequence variation */
    SequenceVariation getProteinVariation();

    /** @return the feature type */
    AnnotationCategory getType();

    /** @return an instance of isoform */
    Isoform buildIsoform() throws UnknownIsoformException;

    /** @return a new instance of validator specific to this sequence feature */
    <SF extends SequenceFeature> SequenceFeatureValidator<SF> newValidator(SingleFeatureQuery query);
}
