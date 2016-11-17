package org.nextprot.api.isoform.mapper.domain;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.nextprot.api.isoform.mapper.domain.impl.exception.UndefinedFeatureQueryException;

import java.util.Collections;
import java.util.List;

/**
 * Single feature query
 */
public class SingleFeatureQuery extends BaseFeatureQuery {

	private static final long serialVersionUID = 20161117L;

	private String accession;
    private String feature;
    private String featureType;
    private boolean propagableFeature;

    public SingleFeatureQuery(){
    	//because of json
    }

    public SingleFeatureQuery(String feature, String featureType, String accession) {

        Preconditions.checkNotNull(feature);
        Preconditions.checkNotNull(featureType);

        this.accession = accession;
        this.feature = feature;
        this.featureType = featureType;
    }

    public void checkFeatureQuery() throws FeatureQueryException {

        super.checkFeatureQuery();

        if (feature.isEmpty())
            throw new UndefinedFeatureQueryException(this);
    }

    /**
     * @return the nextprot entry accession number (example: NX_P01308)
     */
    @Override
    public String getAccession() {
        return accession;
    }

    /**
     * @return the string formatted feature
     */
    public String getFeature() {
        return feature;
    }

    @Override
    public List<String> getFeatureList() {

        return Collections.singletonList(feature);
    }

    /**
     * @return the annotation category as string
     */
    @Override
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

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }
}
