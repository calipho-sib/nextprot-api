package com.nextprot.api.isoform.mapper.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/*
{
"query": {
    "accession": "NX_Q9UI33",
    "feature": "SCN11A-p.Leu1158Pro",
    "feature-type": "VARIANT", // annotation category
    "propagate": "true"
},
"success": true,
"data": {
    "NX_Q9UI33-1": {
        "mapped": true,
        "range": {
            "begin": 1158,
            "end": 1158
        }
    },
    "NX_Q9UI33-2": {
        "mapped": false
    },
    "NX_Q9UI33-3": {
        "mapped": true,
        "range": {
            "begin": 1120,
            "end": 1120
            }
        }
    }
}
*/
public class MappedIsoformsFeatureSuccess extends MappedIsoformsFeatureResult {

    private final Map<String, MappedIsoformFeatureResult> value;

    public MappedIsoformsFeatureSuccess(Query query) {
        super(query);

        value = new HashMap<>();
    }

    public void addMappedIsoformFeature(String isoformName, int firstPosition, int lastPosition) {

        MappedIsoformFeatureResult result = new MappedIsoformFeatureResult();
        result.setIsoformName(isoformName);
        result.setFirstIsoSeqPos(firstPosition);
        result.setLastIsoSeqPos(lastPosition);

        value.put(isoformName, result);
    }

    public void addNonMappedIsoformFeature(String isoformName) {

        MappedIsoformFeatureResult result = new MappedIsoformFeatureResult();
        result.setIsoformName(isoformName);

        value.put(isoformName, result);
    }

    /**
     * Get isoform feature of the specified isoform
     *
     * @param isoformName isoform name
     * @return IsoformFeature or null if isoformName was not found
     */
    public MappedIsoformFeatureResult getMappedIsoformFeatureResult(String isoformName) {

        return value.get(isoformName);
    }

    public boolean hasMappedIsoformFeatureResult(String isoformName) {

        return value.containsKey(isoformName);
    }

    /**
     * @return the number of mapped isoform feature result
     */
    public int countMappedIsoformFeatureResults() {

        return value.size();
    }

    @Override
    protected String getContentName() {
        return "data";
    }

    @Override
    protected Object getContentValue() {
        return value;
    }

    @Override
    public boolean isSuccess() {
        return !value.isEmpty();
    }

    public static class MappedIsoformFeatureResult implements Serializable {

        private static final long serialVersionUID = 1L;

        private String isoformName;
        private Integer firstIsoSeqPos;
        private Integer lastIsoSeqPos;

        public String getIsoformName() {
            return isoformName;
        }

        public void setIsoformName(String isoformName) {
            this.isoformName = isoformName;
        }

        public Integer getFirstIsoSeqPos() {
            return firstIsoSeqPos;
        }

        public void setFirstIsoSeqPos(Integer firstIsoSeqPos) {
            this.firstIsoSeqPos = firstIsoSeqPos;
        }

        public Integer getLastIsoSeqPos() {
            return lastIsoSeqPos;
        }

        public void setLastIsoSeqPos(Integer lastIsoSeqPos) {
            this.lastIsoSeqPos = lastIsoSeqPos;
        }

        public boolean isMapped() {
            return firstIsoSeqPos != null && lastIsoSeqPos != null;
        }
    }
}
