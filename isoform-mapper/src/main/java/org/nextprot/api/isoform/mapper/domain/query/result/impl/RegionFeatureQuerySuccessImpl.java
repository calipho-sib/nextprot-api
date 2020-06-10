package org.nextprot.api.isoform.mapper.domain.query.result.impl;

import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.utils.seqmap.GeneMasterCodonPosition;
import org.nextprot.api.core.utils.seqmap.IsoformSequencePositionMapper;
import org.nextprot.api.isoform.mapper.domain.query.RegionalFeatureQuery;
import org.nextprot.api.isoform.mapper.domain.query.result.FeatureQuerySuccess;

import java.util.Map;
import java.util.TreeMap;

public class RegionFeatureQuerySuccessImpl extends BaseFeatureQueryResult<RegionalFeatureQuery> implements FeatureQuerySuccess {

    private static final long serialVersionUID = 20180815L;
    private final Map<String, SingleFeatureQuerySuccessImpl.IsoformFeatureResult> data;

    public RegionFeatureQuerySuccessImpl(RegionalFeatureQuery query, Isoform isoform) {
        super(query);
        data = new TreeMap<>();
        addMappedFeature(isoform, query.getRegionStart(), query.getRegionEnd());
    }

    public void addMappedFeature(Isoform isoform, int firstIsoPosition, int lastIsoPosition) {

        SingleFeatureQuerySuccessImpl.IsoformFeatureResult result = new SingleFeatureQuerySuccessImpl.IsoformFeatureResult();

        result.setIsoformAccession(isoform.getIsoformAccession());
        result.setIsoformName(isoform.getMainEntityName().getName());
        result.setBeginIsoformPosition(firstIsoPosition);
        result.setEndIsoformPosition(lastIsoPosition);
        result.setCanonical(isoform.isCanonicalIsoform());

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

    /**
     * Get isoform feature of the specified isoform
     *
     * @param isoformName isoform name
     * @return IsoformFeature or null if isoformName was not found
     */
    public SingleFeatureQuerySuccessImpl.IsoformFeatureResult getIsoformFeatureResult(String isoformName) {
        return data.get(isoformName);
    }

    public Map<String, SingleFeatureQuerySuccessImpl.IsoformFeatureResult> getData() {
        return data;
    }


    @Override
    public boolean isSuccess() {
        return !data.isEmpty();
    }
}
