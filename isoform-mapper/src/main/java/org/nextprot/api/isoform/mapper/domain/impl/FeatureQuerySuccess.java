package org.nextprot.api.isoform.mapper.domain.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.nextprot.api.core.utils.seqmap.GeneMasterCodonPosition;
import org.nextprot.api.core.utils.seqmap.IsoformSequencePositionMapper;
import org.nextprot.api.isoform.mapper.domain.FeatureQuery;
import org.nextprot.api.isoform.mapper.domain.FeatureQueryResult;
import org.nextprot.api.isoform.mapper.domain.SequenceFeature;
import org.nextprot.api.core.domain.Entry;
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
    private final Entry entry;

    public FeatureQuerySuccess(Entry entry, FeatureQuery query, SequenceFeature feature) {
        super(query);

        this.entry = entry;

        data = new TreeMap<>();

        this.feature = feature;

        addMappedFeature(feature.getIsoform(entry),
                feature.getProteinVariation().getFirstChangingAminoAcidPos(),
                feature.getProteinVariation().getLastChangingAminoAcidPos());
    }

    @JsonIgnore
    public Entry getEntry() {
        return entry;
    }

    public void addMappedFeature(Isoform isoform, int firstIsoPosition, int lastIsoPosition) {

        IsoformFeatureResult result = new IsoformFeatureResult();

        result.setIsoformAccession(isoform.getUniqueName());
        result.setIsoformName(isoform.getMainEntityName().getName());
        result.setBeginIsoformPosition(firstIsoPosition);
        result.setEndIsoformPosition(lastIsoPosition);
        result.setCanonical(isoform.isCanonicalIsoform());
        result.setIsoSpecificFeature(
                feature.formatIsoSpecificFeature(isoform,
                        firstIsoPosition, lastIsoPosition));

        GeneMasterCodonPosition firstCodonOnMaster =
                IsoformSequencePositionMapper.getCodonPositionsOnMaster(firstIsoPosition, isoform);

        GeneMasterCodonPosition lastCodonOnMaster =
                IsoformSequencePositionMapper.getCodonPositionsOnMaster(lastIsoPosition, isoform);

        if (firstCodonOnMaster.isValid() && lastCodonOnMaster.isValid()) {
            result.setBeginMasterPosition(firstCodonOnMaster.getNucleotidePosition(0));
            result.setEndMasterPosition(lastCodonOnMaster.getNucleotidePosition(2));
        }

        data.put(result.getIsoformAccession(), result);
    }

    public void addUnmappedFeature(Isoform isoform) {

        IsoformFeatureResult result = new IsoformFeatureResult();
        result.setIsoformAccession(isoform.getUniqueName());
        result.setCanonical(isoform.isCanonicalIsoform());

        data.put(result.getIsoformAccession(), result);
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

        private String isoformAccession;
        private String isoformName;
        private Integer beginIsoformPosition;
        private Integer endIsoformPosition;
        private Integer beginMasterPosition;
        private Integer endMasterPosition;
        private boolean isCanonical;
        private String isoSpecificFeature;

        public IsoformFeatureResult(){
        	
        }

        public IsoformFeatureResult(String isoformAccession, String isoformName, Integer beginIsoformPosition, Integer endIsoformPosition, Integer beginMasterPosition, Integer endMasterPosition, boolean isCanonical, String isoSpecificFeature){
        	this.isoformAccession = isoformAccession;
        	this.isoformName = isoformName;
        	this.beginIsoformPosition = beginIsoformPosition;
        	this.endIsoformPosition = endIsoformPosition;
        	this.beginMasterPosition = beginMasterPosition;
        	this.endMasterPosition = endMasterPosition;
        	this.isCanonical = isCanonical;
        	this.isoSpecificFeature = isoSpecificFeature;
        }

        public String getIsoformAccession() {
            return isoformAccession;
        }

        public void setIsoformAccession(String isoformAccession) {
            this.isoformAccession = isoformAccession;
        }

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
