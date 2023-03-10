package org.nextprot.api.isoform.mapper.domain.query;

import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;
import org.nextprot.api.isoform.mapper.domain.query.BaseFeatureQuery;
import org.nextprot.api.isoform.mapper.domain.query.FeatureQueryException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Multiple queries defined from a list and/or a map of features
 */
@ApiObject(name = "multiple-feature-query", description = "multiple query")
public class MultipleFeatureQuery extends BaseFeatureQuery {

    private static final long serialVersionUID = 20161117L;

    @ApiObjectField(description = "The category type", allowedvalues = {"variant"})
    private String featureType;
    @ApiObjectField(description = "The entry accession number", allowedvalues = {"NX_Q9UI33"})
    private String accession;
    @ApiObjectField(description = "A list of feature types to query", allowedvalues = {"[SCN11A-p.Leu1158Pro]"})
    private List<String> featureList = new ArrayList<>();
    private List<Map<String, String>> featureMaps = new ArrayList<>();

    @Override
    public List<String> getFeatureList() {

        return featureList;
    }

    public void setFeatureList(List<String> featureList) {
        this.featureList.addAll(featureList);
    }

    @Override
    public String getFeatureType() {
        return featureType;
    }

    public void setFeatureType(String featureType) {
        this.featureType = featureType;
    }

    @Override
    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    public List<Map<String, String>> getFeatureMaps() {

        return featureMaps;
    }

    public void setFeatureMaps(List<Map<String, String>> featureMaps) {

        this.featureMaps.addAll(featureMaps);
    }

    public void checkFeatureQuery() throws FeatureQueryException {

        checkAccessionNotIsoform();
        checkAnnotationCategoryExists();
        checkFeatureNonEmpty();
    }
}
