package org.nextprot.api.isoform.mapper.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.app.ApplicationContextProvider;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.service.impl.AnnotationServiceImpl;
import org.nextprot.api.core.utils.seqmap.IsoformSequencePositionMapper;
import org.nextprot.api.isoform.mapper.domain.query.RegionalFeatureQuery;
import org.nextprot.api.isoform.mapper.domain.query.result.impl.BaseFeatureQueryResult;
import org.nextprot.api.isoform.mapper.domain.query.result.impl.RegionFeatureQuerySuccessImpl;
import org.nextprot.api.isoform.mapper.service.RegionIsoformMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Regional isoform mapping service, which maps regional features on to other isoforms
 */
@Service
public class RegionIsoformMappingServiceImpl implements RegionIsoformMappingService {

    private static final Log LOGGER = LogFactory.getLog(AnnotationServiceImpl.class);

    @Autowired
    private IsoformService isoformService;

    /**
     * Propagates feature on to other isoforms given the feature query
     * @param query
     * @return result
     */
    public BaseFeatureQueryResult propagateFeature(RegionalFeatureQuery query) {

        // Should build the isoform from the query
        Isoform isoform = ApplicationContextProvider.getApplicationContext().getBean(IsoformService.class)
                .getIsoformByNameOrCanonical(query.getAccession());

        // Validates
        if(validate(query, isoform)) {

            // Propagate
            return propagate(query, isoform);
        } else {
            return null;
        }
    }

    /**
     * Validates if a given subsequence exists in a given isoform
     * @param query
     * @param isoform
     * @return validity
     */
    private boolean validate(RegionalFeatureQuery query, Isoform isoform) {
        int regionStart = query.getRegionStart();
        int regionEnd = query.getRegionEnd();
        String regionFromQuery = query.getRegionSequence();
        String regionFromIsoform = isoform.getSequence().substring(regionStart - 1, regionEnd);

        if(regionFromQuery != null && regionFromIsoform != null) {
            // Alow tolerance
            return regionFromQuery.equals(regionFromIsoform);
        } else {
            return false;
        }
    }

    /**
     * Propagates the given region on the protein sequence into other isoforms
     * @param query
     * @param isoform
     * @return result
     */
    private BaseFeatureQueryResult propagate(RegionalFeatureQuery query, Isoform isoform) {

        int regionStart = query.getRegionStart();
        int regionEnd = query.getRegionEnd();
        int regionLength = regionEnd - regionStart + 1;
        String region = isoform.getSequence().substring(regionStart - 1, regionEnd);

        RegionFeatureQuerySuccessImpl result = new RegionFeatureQuerySuccessImpl(query);

        for (Isoform targetIsoform : isoformService.getOtherIsoforms(isoform.getIsoformAccession())) {

            // Propagate the first position
            Integer targetIsoformRegionStart = IsoformSequencePositionMapper.getProjectedPosition(isoform, regionStart, targetIsoform);
            Integer targetIsoformRegionEnd = IsoformSequencePositionMapper.getProjectedPosition(isoform, regionEnd, targetIsoform);

            if(targetIsoformRegionStart == null || targetIsoformRegionEnd == null) {
                LOGGER.info("Project start/end position does not exist");
                continue;
            } else {
                int projectedSequenceLegth = targetIsoformRegionEnd - targetIsoformRegionStart + 1;
                // Check if the sub sequence exists consecutively on the other isoform
                if(regionLength == projectedSequenceLegth) {
                    String targetIsoformRegion = targetIsoform.getSequence().substring(targetIsoformRegionStart - 1, targetIsoformRegionEnd);

                    // If the sub sequence is shorter or equal to 30, an exact match is required
                    if(projectedSequenceLegth <= 30) {
                        if(region.equals(targetIsoformRegion)) {
                            result.addMappedFeature(isoform, targetIsoformRegionStart, targetIsoformRegionEnd);
                        } else {
                            continue;
                        }
                    } else {
                        // For sub sequences longer than 30 a mismatches are tolerated up to a level
                        int unmatchCount = region.length();
                        boolean matched = true;
                        for(int i = 0; i < region.length(); i++) {
                            if(region.charAt(i) != targetIsoformRegion.charAt(i)) {
                                unmatchCount--;
                                if((float)(unmatchCount/region.length()) > 0.05 ) {
                                    matched = false;
                                    break;
                                }
                            }
                        }
                        if(matched) {
                            // Matching regions
                            LOGGER.info("Matched isform regions with score " + ((regionLength - unmatchCount)/regionLength));
                            result.addMappedFeature(targetIsoform, targetIsoformRegionStart, targetIsoformRegionEnd);
                        } else {
                            continue;
                        }
                    }

                } else {
                    LOGGER.info("Subsequence does not align with projected indices");
                    continue;
                }
            }
        }

        return result;
    }

}

