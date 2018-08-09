package org.nextprot.api.isoform.mapper.domain.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariation;
import org.nextprot.api.commons.bio.variation.prot.impl.VariantSequenceOperator;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.utils.IsoformUtils;
import org.nextprot.api.core.utils.seqmap.GeneMasterCodonPosition;
import org.nextprot.api.core.utils.seqmap.IsoformSequencePositionMapper;
import org.nextprot.api.isoform.mapper.domain.FeatureQuerySuccess;
import org.nextprot.api.isoform.mapper.domain.SequenceFeature;
import org.nextprot.api.isoform.mapper.domain.SingleFeatureQuery;
import org.nextprot.api.isoform.mapper.domain.impl.exception.UnknownIsoformException;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * Contains successful results of a FeatureQuery on an isoform
 */
public class SingleFeatureQuerySuccessImpl extends BaseFeatureQueryResult<SingleFeatureQuery> implements FeatureQuerySuccess {

	private static final long serialVersionUID = 20161117L;
	private final Map<String, IsoformFeatureResult> data;
    private final transient SequenceFeature feature;
    private final transient Entry entry;
    
    public SingleFeatureQuerySuccessImpl(Entry entry, SingleFeatureQuery query, SequenceFeature feature) {
        super(query);

        this.entry = entry;

        data = new TreeMap<>();

        this.feature = feature;

        try {
            addMappedFeature(IsoformUtils.getIsoformByNameOrCanonical(entry, feature.getIsoform().getIsoformAccession()),
                    feature.getProteinVariation().getVaryingSequence().getFirstAminoAcidPos(),
                    feature.getProteinVariation().getVaryingSequence().getLastAminoAcidPos());
        } catch (UnknownIsoformException e) {
            throw new NextProtException(e);
        }
    }

    @JsonIgnore
    public Entry getEntry() {
        return entry;
    }

    public void addMappedFeature(Isoform isoform, int firstIsoPosition, int lastIsoPosition) {

        IsoformFeatureResult result = new IsoformFeatureResult();

        result.setIsoformAccession(isoform.getIsoformAccession());
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

        SequenceVariation proteinVariation = newIsoformSequenceVariation(feature.getProteinVariation(), firstIsoPosition, lastIsoPosition);

        setVariantSequenceBuildingInfos(result, isoform.getSequence(), proteinVariation);

        data.put(result.getIsoformAccession(), result);
    }

    /* create new SequenceVariation isoform specific */
    private SequenceVariation newIsoformSequenceVariation(SequenceVariation sequenceVariation, int firstIsoPosition, int lastIsoPosition) {

        SequenceVariationMutable newSequenceVariation = new SequenceVariationMutable();

        VaryingSequenceMutable vs = VaryingSequenceMutable.valueOf(sequenceVariation.getVaryingSequence());

        vs.setFirstPos(firstIsoPosition);
        vs.setLastPos(lastIsoPosition);

        newSequenceVariation.setVaryingSequence(vs);
        newSequenceVariation.setSequenceChange(sequenceVariation.getSequenceChange());

        return newSequenceVariation;
    }

    private void setVariantSequenceBuildingInfos(IsoformFeatureResult result, String sequence, SequenceVariation proteinVariation) {

        Optional<VariantSequenceOperator> optOperator =
                VariantSequenceOperator.findOperator(proteinVariation.getSequenceChange());

        if (optOperator.isPresent()) {

            VariantSequenceOperator op = optOperator.get();

            result.setVariation(
                    op.getAminoAcidTargetStringInReferenceSequence(sequence, proteinVariation.getVaryingSequence()),
                    op.getAminoAcidReplacementString(sequence, proteinVariation),
                    op.selectBeginPositionInReferenceSequence(proteinVariation.getVaryingSequence()),
                    op.selectEndPositionInReferenceSequence(proteinVariation.getVaryingSequence()),
                    proteinVariation.getSequenceChange().getType().name().toLowerCase()
            );
        }
    }

    public void addUnmappedFeature(Isoform isoform) {

        IsoformFeatureResult result = new IsoformFeatureResult();
        result.setIsoformAccession(isoform.getIsoformAccession());
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

        private static final long serialVersionUID = 3L;

        private final static String ORIGINAL = "original";
        private final static String VARIATION = "variant";
        private final static String BEGIN_POS = "startPosition";
        private final static String END_POS = "endPosition";
        private final static String TYPE = "type";

        private String isoformAccession;
        private String isoformName;
        private Integer beginIsoformPosition;
        private Integer endIsoformPosition;
        private Integer beginMasterPosition;
        private Integer endMasterPosition;
        private boolean isCanonical;
        private String isoSpecificFeature;

        private Map<String, Object> variationDesc;

        // empty constructor for json serialization
        public IsoformFeatureResult(){
            variationDesc = new LinkedHashMap<>();
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
            variationDesc = new LinkedHashMap<>();
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

        public void setVariation(String original, String variation, int beginIsoformPosition, int endIsoformPosition, String type) {

            variationDesc.put(ORIGINAL, original);
            variationDesc.put(VARIATION, variation);
            variationDesc.put(BEGIN_POS, beginIsoformPosition);
            variationDesc.put(END_POS, endIsoformPosition);
            variationDesc.put(TYPE, type);
        }

        public Map<String, Object> getVariation() {
            return variationDesc;
        }
    }
}
