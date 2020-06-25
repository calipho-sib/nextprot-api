package org.nextprot.api.isoform.mapper.domain.query;


import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.isoform.mapper.domain.impl.exception.UndefinedFeatureQueryException;
import org.nextprot.api.isoform.mapper.domain.impl.exception.UnknownFeatureQueryTypeException;

public abstract class BaseFeatureQuery implements FeatureQuery {

    @Override
    public void checkFeatureQuery() throws FeatureQueryException {

        checkAccessionNotIsoform();
        checkAnnotationCategoryExists();
        checkFeatureNonEmpty();
    }

    void checkAccessionNotIsoform() {

        if (getAccession() != null && getAccession().contains("-")) {
            int dashIndex = getAccession().indexOf("-");

            throw new NextProtException("Invalid entry accession " + getAccession()
                    + ": " + getAccession().substring(0, dashIndex)+" was expected");
        }
    }

    void checkAnnotationCategoryExists() throws FeatureQueryException {

        if (!AnnotationCategory.hasAnnotationByApiName(getFeatureType()))
            throw new UnknownFeatureQueryTypeException(this);
    }

    void checkFeatureNonEmpty() throws FeatureQueryException {

        if (getFeatureList().isEmpty())
            throw new UndefinedFeatureQueryException(this);
    }

}
