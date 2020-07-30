package org.nextprot.api.isoform.mapper.domain.query;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
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


    private List<TargetIsoformRegion> targetIsoformRegions;


    public RegionalFeatureQuery(String accession, String featureType, int regionStart, int regionEnd) {
        this.accession = accession;
        this.featureType = featureType;
        this.regionStart = regionStart;
        this.regionEnd = regionEnd;
        this.targetIsoformRegions = new ArrayList<>();
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

    public List<TargetIsoformRegion> getTargetIsoformRegions() { return targetIsoformRegions; }

    public void setTargetIsoformRegions(JSONArray targetIsoformRegionsJSON) {
        targetIsoformRegionsJSON.forEach(tir -> {
            JSONObject targetIsoformRegionJSON = (JSONObject)tir;
            int begin = Integer.parseInt(targetIsoformRegionJSON.get("begin").toString());
            int end = Integer.parseInt(targetIsoformRegionJSON.get("end").toString());
            String isoformAccession = targetIsoformRegionJSON.get("isoformAccession").toString();

            TargetIsoformRegion targetIsoformRegion = new TargetIsoformRegion();
            targetIsoformRegion.setIsoformAccession(isoformAccession);
            targetIsoformRegion.setRegionBegin(begin);
            targetIsoformRegion.setRegionEnd(end);
            targetIsoformRegions.add(targetIsoformRegion);
        });
    }

    public void setRegionSequence(String sequence) {
        regionSequence = sequence;
    }

    public String getRegionSequence() {
        return regionSequence;
    }


    public static class TargetIsoformRegion {

        private String isoformAccession;

        private int regionBegin;

        private int regionEnd;

        public void setIsoformAccession(String isoformAccession) {
            this.isoformAccession = isoformAccession;
        }

        public void setRegionBegin(int regionBegin) {
            this.regionBegin = regionBegin;
        }

        public void setRegionEnd(int regionEnd) {
            this.regionEnd = regionEnd;
        }

        public String getIsoformAccession() {
            return isoformAccession;
        }

        public int getRegionBegin() {
            return regionBegin;
        }

        public int getRegionEnd() {
            return regionEnd;
        }
    }
}
