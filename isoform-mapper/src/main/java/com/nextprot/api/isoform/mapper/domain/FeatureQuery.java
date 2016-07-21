package com.nextprot.api.isoform.mapper.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Preconditions;
import com.nextprot.api.isoform.mapper.domain.impl.exception.UndefinedFeatureQueryException;
import com.nextprot.api.isoform.mapper.domain.impl.exception.UnknownFeatureQueryTypeException;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.Entry;

import java.io.Serializable;

public class FeatureQuery implements Serializable {

    private final Entry entry;
    private final String feature;
    private final String featureType;
    private boolean propagableFeature;

    public FeatureQuery(Entry entry, String feature, String featureType) throws FeatureQueryException {

        Preconditions.checkNotNull(entry);
        Preconditions.checkNotNull(feature);
        Preconditions.checkNotNull(featureType);

        this.entry = entry;
        this.feature = feature;
        this.featureType = featureType;

        checkStates();
    }

    private void checkStates() throws FeatureQueryException {

        if (!AnnotationCategory.hasAnnotationByApiName(featureType))
            throw new UnknownFeatureQueryTypeException(this);
        else if (feature.isEmpty())
            throw new UndefinedFeatureQueryException(this);
    }

    @JsonIgnore
    public Entry getEntry() {
        return entry;
    }

    /**
     * @return the nextprot entry accession number (example: NX_P01308)
     */
    public String getAccession() {
        return entry.getUniqueName();
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
