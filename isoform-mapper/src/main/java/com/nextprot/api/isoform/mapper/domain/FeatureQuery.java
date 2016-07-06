package com.nextprot.api.isoform.mapper.domain;

import com.google.common.base.Preconditions;
import com.nextprot.api.isoform.mapper.domain.impl.InvalidNextprotAccessionException;
import com.nextprot.api.isoform.mapper.domain.impl.UndefinedFeatureQueryException;
import com.nextprot.api.isoform.mapper.domain.impl.UnknownFeatureQueryTypeException;
import org.nextprot.api.commons.constants.AnnotationCategory;

import java.io.Serializable;

public class FeatureQuery implements Serializable {

    private final String accession;
    private final String feature;
    private final String featureType;
    private final boolean propagableFeature;

    public FeatureQuery(String accession, String feature, String featureType, boolean propagableFeature) throws FeatureQueryException {

        Preconditions.checkNotNull(accession);
        Preconditions.checkNotNull(feature);
        Preconditions.checkNotNull(featureType);

        this.accession = accession;
        this.feature = feature;
        this.featureType = featureType;
        this.propagableFeature = propagableFeature;

        checkStates();
    }

    private void checkStates() throws FeatureQueryException {

        if (!AnnotationCategory.hasAnnotationByApiName(featureType))
            throw new UnknownFeatureQueryTypeException(this);
        else if (!accession.startsWith("NX_"))
            throw new InvalidNextprotAccessionException(this);
        else if (feature.isEmpty())
            throw new UndefinedFeatureQueryException(this);
    }

    /**
     * @return the nextprot accession number of the isoform (example: NX_P01308 or NX_P01308-1)
     */
    public String getAccession() {
        return accession;
    }

    /**
     * @return the string formatted feature
     */
    public String getFeature() {
        return feature;
    }

    /**
     * @return the annotation category as string
     */
    public String getFeatureType() {
        return featureType;
    }

    /**
     * @return true if feature should be propagated to other isoforms
     */
    public boolean isFeaturePropagable() {
        return propagableFeature;
    }
}
