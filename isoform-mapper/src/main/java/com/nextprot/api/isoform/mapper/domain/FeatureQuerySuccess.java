package com.nextprot.api.isoform.mapper.domain;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

/**
 * Contains successful results of a FeatureQuery on an isoform
 */
public class FeatureQuerySuccess extends FeatureQueryResult {

    private final Map<String, IsoformFeatureResult> data;

    public FeatureQuerySuccess(FeatureQuery query) {
        super(query);

        data = new TreeMap<>();
    }

    public void addMappedFeature(String isoformName, int firstPosition, int lastPosition) {

        IsoformFeatureResult result = new IsoformFeatureResult();
        result.setIsoformName(isoformName);
        result.setFirstIsoSeqPos(firstPosition);
        result.setLastIsoSeqPos(lastPosition);

        data.put(isoformName, result);
    }

    public void addUnmappedFeature(String isoformName) {

        IsoformFeatureResult result = new IsoformFeatureResult();
        result.setIsoformName(isoformName);

        data.put(isoformName, result);
    }

    /**
     * Get isoform feature of the specified isoform
     *
     * @param isoformName isoform name
     * @return IsoformFeature or null if isoformName was not found
     */
    public IsoformFeatureResult getIsoformFeatureResult(String isoformName) {

        return data.get(isoformName);
    }

    public Map<String, IsoformFeatureResult> getData() {
        return data;
    }

    @Override
    public boolean isSuccess() {
        return !data.isEmpty();
    }

    public static class IsoformFeatureResult implements Serializable {

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
