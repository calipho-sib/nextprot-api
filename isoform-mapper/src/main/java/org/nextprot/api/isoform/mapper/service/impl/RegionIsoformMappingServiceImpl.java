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

    private static final Log LOGGER = LogFactory.getLog(RegionIsoformMappingServiceImpl.class);

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
            RegionFeatureQuerySuccessImpl result = new RegionFeatureQuerySuccessImpl(query, isoform);
            return result;
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
            // Allow tolerance
            float matchingScore = getMismatchCount(regionFromQuery,regionFromIsoform);
            if(matchingScore == -1) {
                LOGGER.info("accession:"+ query.getAccession()+",region:"+ regionFromQuery+",region_np:"+regionFromIsoform+ "step1:rejected_sequence_length_notequal");
                return false;
            }

            if(matchingScore >= 0.96) {
                LOGGER.info("accession:"+ query.getAccession()+",region:"+ regionFromQuery+",region_np:"+regionFromIsoform+ "step1:matched_with_score_"+matchingScore);
                return true;
            } else {
                LOGGER.info("accession:"+ query.getAccession()+",region:"+ regionFromQuery+",region_np:"+regionFromIsoform+ "step1:rejected_with_score_"+matchingScore);
                return false;
            }
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

        RegionFeatureQuerySuccessImpl result = new RegionFeatureQuerySuccessImpl(query, isoform);

        for (Isoform targetIsoform : isoformService.getOtherIsoforms(isoform.getIsoformAccession())) {

            // Propagate the first position
            Integer targetIsoformRegionStart = IsoformSequencePositionMapper.getProjectedPosition(isoform, regionStart, targetIsoform);
            Integer targetIsoformRegionEnd = IsoformSequencePositionMapper.getProjectedPosition(isoform, regionEnd, targetIsoform);

            if(targetIsoformRegionStart == null || targetIsoformRegionEnd == null) {
                LOGGER.warn("Project start/end position does not exist");
                continue;
            } else {
                int projectedSequenceLegth = targetIsoformRegionEnd - targetIsoformRegionStart + 1;
                // Check if the sub sequence exists consecutively on the other isoform
                String targetIsoformRegion = targetIsoform.getSequence().substring(targetIsoformRegionStart - 1, targetIsoformRegionEnd);
                if(regionLength == projectedSequenceLegth) {
                    // If the sub sequence is shorter or equal to 30, an exact match is required
                    if(projectedSequenceLegth <= 30) {
                        boolean matched = region.equals(targetIsoformRegion);
                        LOGGER.info("accession:"+ targetIsoform.getIsoformAccession()+",region:"+ region+",region_length:"+region.length()+",region_np_isoform:"+targetIsoformRegion+",target_isoform_region_length:"+targetIsoformRegion.length()+ "step2:matched_"+matched);
                        if(matched) {
                            result.addMappedFeature(targetIsoform, targetIsoformRegionStart, targetIsoformRegionEnd);
                        } else {
                            continue;
                        }
                    } else {
                        // For sub sequences longer than 30 a mismatches are tolerated up to a level
                        float matchingScore = getMismatchCount(region,targetIsoformRegion);
                        if(matchingScore > 0.96) {
                            // Matching regions
                            LOGGER.info("accession:"+ targetIsoform.getIsoformAccession()+",region:"+ region+",region_length:"+region.length()+",region_np_isoform:"+targetIsoformRegion+",target_isoform_region_length:"+targetIsoformRegion.length()+ "step2:matched_with_score_"+matchingScore);
                            result.addMappedFeature(targetIsoform, targetIsoformRegionStart, targetIsoformRegionEnd);
                        } else {
                            LOGGER.info("accession:"+ targetIsoform.getIsoformAccession()+",region:"+ region+",region_length:"+region.length()+",region_np_isoform:"+targetIsoformRegion+",target_isoform_region_length:"+targetIsoformRegion.length()+ "step2:rejected_with_score_"+matchingScore);
                            continue;
                        }
                    }

                } else {
                    LOGGER.info("accession:"+ targetIsoform.getIsoformAccession()+",region:"+ region+",region_length:"+region.length()+",region_np_isoform:"+targetIsoformRegion+",target_isoform_region_length:"+targetIsoformRegion.length()+ "step2:rejected_sequence_length_notequal");
                    continue;
                }
            }
        }

        return result;
    }


    private float getMismatchCount(String s, String r) {
        if(s.length() != r.length()) {
            return -1;
        }

        int misMatchCount = s.length();
        for(int i = 0; i < s.length(); i++) {
            if(s.charAt(i) != r.charAt(i)) {
                misMatchCount--;
            }
        }

        return (float)(s.length() - misMatchCount)/s.length();
    }
}

