package com.nextprot.api.isoform.mapper.domain;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

public class MappedIsoformsFeatureSuccess extends MappedIsoformsFeatureResult {

    private final Map<String, MappedIsoformFeatureResult> data;

    public MappedIsoformsFeatureSuccess(Query query) {
        super(query);

        data = new TreeMap<>();
    }

    public void addMappedIsoformFeature(String isoformName, int firstPosition, int lastPosition) {

        MappedIsoformFeatureResult result = new MappedIsoformFeatureResult();
        result.setIsoformName(isoformName);
        result.setFirstIsoSeqPos(firstPosition);
        result.setLastIsoSeqPos(lastPosition);

        data.put(isoformName, result);
    }

    public void addNonMappedIsoformFeature(String isoformName) {

        MappedIsoformFeatureResult result = new MappedIsoformFeatureResult();
        result.setIsoformName(isoformName);

        data.put(isoformName, result);
    }

    /**
     * Get isoform feature of the specified isoform
     *
     * @param isoformName isoform name
     * @return IsoformFeature or null if isoformName was not found
     */
    public MappedIsoformFeatureResult getMappedIsoformFeatureResult(String isoformName) {

        return data.get(isoformName);
    }

    public boolean hasMappedIsoformFeatureResult(String isoformName) {

        return data.containsKey(isoformName);
    }

    /**
     * @return the number of mapped isoform feature result
     */
    public int countMappedIsoformFeatureResults() {

        return data.size();
    }

    public Map<String, MappedIsoformFeatureResult> getData() {
        return data;
    }

    @Override
    public boolean isSuccess() {
        return !data.isEmpty();
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
