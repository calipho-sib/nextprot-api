package org.nextprot.api.isoform.mapper.domain;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.isoform.mapper.domain.impl.exception.UndefinedFeatureQueryException;
import org.nextprot.api.isoform.mapper.domain.impl.exception.UnknownFeatureQueryTypeException;

public class FeatureQuery implements BaseFeatureQuery {

	private static final long serialVersionUID = 20161116L;

	private String accession;
    private String feature;
    private String featureType;
    private boolean propagableFeature;

    public FeatureQuery(){
    	//because of json
    }

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
}
