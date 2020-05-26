package org.nextprot.api.isoform.mapper.domain.query;

import org.nextprot.api.isoform.mapper.domain.query.BaseFeatureQuery;

import java.util.List;

/**
 * Represents a feature query, which spans across a region of the protein sequence
 */
public class RegionalFeatureQuery extends BaseFeatureQuery {

    private String accession;

    private String featureType;

    /**
     * Start position of the region on the sequence
     */
    private int regionStart;

    /**
     * End position of the region on the sequence
     */
    private int regionEnd;

    /**
     * Sequence of the region query
     */
    private String regionSequence;


    public RegionalFeatureQuery(String accession, String featureType, int regionStart, int regionEnd) {
        this.accession = accession;
        this.featureType = featureType;
        this.regionStart = regionStart;
        this.regionEnd = regionEnd;
    }

    @Override
    public List<String> getFeatureList() {
        return null;
    }

    @Override
    public String getFeatureType() {
        return featureType;
    }

    @Override
    public String getAccession() {
        return accession;
    }

    public int getRegionStart() {
        return regionStart;
    }

    public int getRegionEnd() {
        return regionEnd;
    }

    public void setRegionSequence(String sequence) {
        regionSequence = sequence;
    }

    public String getRegionSequence() {
        return regionSequence;
    }
}
