package com.nextprot.api.isoform.mapper.domain.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nextprot.api.isoform.mapper.domain.EntryIsoform;
import com.nextprot.api.isoform.mapper.domain.FeatureQuery;
import com.nextprot.api.isoform.mapper.domain.FeatureQueryResult;
import com.nextprot.api.isoform.mapper.domain.SequenceFeature;
import org.nextprot.api.core.domain.Isoform;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

/**
 * Contains successful results of a FeatureQuery on an isoform
 */
public class FeatureQuerySuccess extends FeatureQueryResult {

    private final Map<String, IsoformFeatureResult> data;
    private final SequenceFeature feature;

    public FeatureQuerySuccess(FeatureQuery query, SequenceFeature feature) {
        super(query);

        data = new TreeMap<>();

        this.feature = feature;

        addMappedFeature(getQuery().getEntryIsoform().getIsoform(),
                feature.getVariation().getFirstChangingAminoAcidPos(),
                feature.getVariation().getLastChangingAminoAcidPos());
    }

    public void addMappedFeature(Isoform isoform, int firstPosition, int lastPosition) {

        IsoformFeatureResult result = new IsoformFeatureResult();
        result.setIsoformName(isoform.getUniqueName());
        result.setBeginIsoformPosition(firstPosition);
        result.setEndIsoformPosition(lastPosition);
        result.setCanonical(isoform.isCanonicalIsoform());
        result.setIsoSpecificFeature(
                feature.formatIsoSpecificFeature(EntryIsoform.getIsoformNumber(isoform),
                        firstPosition, lastPosition));

        data.put(result.getIsoformName(), result);
    }

    public void addUnmappedFeature(Isoform isoform) {

        IsoformFeatureResult result = new IsoformFeatureResult();
        result.setIsoformName(isoform.getUniqueName());
        result.setCanonical(isoform.isCanonicalIsoform());

        data.put(result.getIsoformName(), result);
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

    @JsonIgnore
    public SequenceFeature getIsoformSequenceFeature() {
        return feature;
    }

    @Override
    public boolean isSuccess() {
        return !data.isEmpty();
    }

    public static class IsoformFeatureResult implements Serializable {

        private static final long serialVersionUID = 2L;

        private String isoformName;
        private Integer beginIsoformPosition;
        private Integer endIsoformPosition;
        private Integer beginMasterPosition;
        private Integer endMasterPosition;
        private boolean isCanonical;
        private String isoSpecificFeature;

        public String getIsoformName() {
            return isoformName;
        }

        public void setIsoformName(String isoformName) {
            this.isoformName = isoformName;
        }

        public Integer getBeginIsoformPosition() {
            return beginIsoformPosition;
        }

        public void setBeginIsoformPosition(Integer beginIsoformPosition) {
            this.beginIsoformPosition = beginIsoformPosition;
        }

        public Integer getEndIsoformPosition() {
            return endIsoformPosition;
        }

        public void setEndIsoformPosition(Integer endIsoformPosition) {
            this.endIsoformPosition = endIsoformPosition;
        }

        public Integer getBeginMasterPosition() {
            return beginMasterPosition;
        }

        public void setBeginMasterPosition(Integer beginMasterPosition) {
            this.beginMasterPosition = beginMasterPosition;
        }

        public Integer getEndMasterPosition() {
            return endMasterPosition;
        }

        public void setEndMasterPosition(Integer endMasterPosition) {
            this.endMasterPosition = endMasterPosition;
        }

        public boolean isCanonical() {
            return isCanonical;
        }

        public void setCanonical(boolean canonical) {
            isCanonical = canonical;
        }

        public boolean isMapped() {
            return beginIsoformPosition != null && endIsoformPosition != null;
        }

        public String getIsoSpecificFeature() {
            return isoSpecificFeature;
        }

        public void setIsoSpecificFeature(String isoSpecificFeature) {
            this.isoSpecificFeature = isoSpecificFeature;
        }
    }
}
