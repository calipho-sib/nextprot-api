package com.nextprot.api.isoform.mapper.domain.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nextprot.api.isoform.mapper.domain.FeatureQuery;
import com.nextprot.api.isoform.mapper.domain.FeatureQueryResult;
import com.nextprot.api.isoform.mapper.utils.EntryIsoform;
import org.nextprot.api.commons.bio.variation.SequenceVariation;
import org.nextprot.api.core.domain.Isoform;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

/**
 * Contains successful results of a FeatureQuery on an isoform
 */
public class FeatureQuerySuccess extends FeatureQueryResult {

    private final Map<String, IsoformFeatureResult> data;
    private EntryIsoform entryIsoform;
    private SequenceVariation isoformSequenceVariation;

    public FeatureQuerySuccess(FeatureQuery query) {
        super(query);

        data = new TreeMap<>();
    }

    public void setSequenceVariation(EntryIsoform entryIsoform, SequenceVariation variation) {

        this.isoformSequenceVariation = variation;
        this.entryIsoform = entryIsoform;

        addMappedFeature(entryIsoform.getIsoform(), variation.getFirstChangingAminoAcidPos(), variation.getLastChangingAminoAcidPos());
    }

    public void addMappedFeature(Isoform isoform, int firstPosition, int lastPosition) {

        IsoformFeatureResult result = new IsoformFeatureResult();
        result.setIsoformName(isoform.getUniqueName());
        result.setFirstIsoSeqPos(firstPosition);
        result.setLastIsoSeqPos(lastPosition);
        result.setCanonical(isoform.isCanonicalIsoform());

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
    public EntryIsoform getEntryIsoform() {
        return entryIsoform;
    }

    @JsonIgnore
    public SequenceVariation getIsoformSequenceVariation() {
        return isoformSequenceVariation;
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
        private boolean isCanonical;

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

        public boolean isCanonical() {
            return isCanonical;
        }

        public void setCanonical(boolean canonical) {
            isCanonical = canonical;
        }

        public boolean isMapped() {
            return firstIsoSeqPos != null && lastIsoSeqPos != null;
        }
    }
}
