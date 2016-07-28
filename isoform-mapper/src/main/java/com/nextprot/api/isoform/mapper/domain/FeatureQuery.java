package com.nextprot.api.isoform.mapper.domain;

import com.google.common.base.Preconditions;
import com.nextprot.api.isoform.mapper.domain.impl.exception.UndefinedFeatureQueryException;
import com.nextprot.api.isoform.mapper.domain.impl.exception.UnknownFeatureQueryTypeException;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;

import java.io.Serializable;

public class FeatureQuery implements Serializable {

    private final String accession;
    private final String feature;
    private final String featureType;
    private boolean propagableFeature;

    public FeatureQuery(String accession, String feature, String featureType) {

        Preconditions.checkNotNull(feature);
        Preconditions.checkNotNull(featureType);

        this.accession = accession;
        this.feature = feature;
        this.featureType = featureType;
    }

    public void checkFeatureQuery() throws FeatureQueryException {

        checkAccessionNotIsoform();
        checkAnnotationCategoryExists();
        checkFeatureNonEmpty();
    }

    private void checkAccessionNotIsoform() {

        if (accession != null && accession.contains("-")) {
            int dashIndex = accession.indexOf("-");

            throw new NextProtException("Invalid entry accession " + accession
                    + ": " + accession.substring(0, dashIndex)+" was expected");
        }
    }

    private void checkAnnotationCategoryExists() throws FeatureQueryException {

        if (!AnnotationCategory.hasAnnotationByApiName(featureType))
            throw new UnknownFeatureQueryTypeException(this);
    }

    private void checkFeatureNonEmpty() throws FeatureQueryException {

        if (feature.isEmpty())
            throw new UndefinedFeatureQueryException(this);
    }

    /**
     * @return the nextprot entry accession number (example: NX_P01308)
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

    public void setPropagableFeature(boolean propagableFeature) {

        this.propagableFeature = propagableFeature;
    }
}
