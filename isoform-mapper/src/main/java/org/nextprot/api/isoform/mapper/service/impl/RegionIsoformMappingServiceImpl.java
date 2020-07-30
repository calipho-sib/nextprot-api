package org.nextprot.api.isoform.mapper.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.text.similarity.LevenshteinDistance;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;


/**
 * Regional isoform mapping service, which maps regional features on to other isoforms
 */
@Service
public class RegionIsoformMappingServiceImpl implements RegionIsoformMappingService {

    private static final Log LOGGER = LogFactory.getLog(RegionIsoformMappingServiceImpl.class);

    @Autowired
    private IsoformService isoformService;

    /**
     * Propagates feature on to other isoforms given the target isoforms
     * In fact, it does not propagate the regions, but validates the regions based on edit distance between the isoform and the target isoform regions
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
            return propagateTargetIsoforms(query, isoform);
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
            float matchingScore = getMatchingScore(regionFromQuery,regionFromIsoform);
            if(matchingScore >= 0.96) {
                LOGGER.info("validating:"+isoform.getIsoformAccession()+",region:"+regionFromQuery+",start:"+regionStart+",end:"+regionEnd+",accession:"+ query.getAccession()+",region:"+ regionFromQuery+",region_np:"+regionFromIsoform+ ",step1:matched_with_score_"+matchingScore);
                return true;
            } else {
                LOGGER.info("validating:"+isoform.getIsoformAccession()+",region:"+regionFromQuery+",start:"+regionStart+",end:"+regionEnd+",accession:"+ query.getAccession()+",region:"+ regionFromQuery+",region_np:"+regionFromIsoform+ ",step1:rejected_with_score_"+matchingScore);
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Validates all the target isoforms
     * @param query
     * @param isoform
     */
    private BaseFeatureQueryResult propagateTargetIsoforms(RegionalFeatureQuery query, Isoform isoform) {

        Map<String, Isoform> targetIsoforms = isoformService.getOtherIsoforms(isoform.getIsoformAccession())
                .stream()
                .collect(Collectors.toMap(Isoform::getIsoformAccession, isoform1 -> isoform1));

        RegionFeatureQuerySuccessImpl result = new RegionFeatureQuerySuccessImpl(query, isoform);
        List<RegionalFeatureQuery.TargetIsoformRegion> targetIsoformRegions = query.getTargetIsoformRegions();
        // Exclude the main/canonical isoform
        targetIsoformRegions.removeIf(targetIsoformRegion -> query.getAccession().equals(targetIsoformRegion.getIsoformAccession()));
        for(RegionalFeatureQuery.TargetIsoformRegion targetIsoformRegion : targetIsoformRegions) {

            int begin = targetIsoformRegion.getRegionBegin();
            int end = targetIsoformRegion.getRegionEnd();
            Isoform targetIsoform = targetIsoforms.get(targetIsoformRegion.getIsoformAccession());
            if(targetIsoform == null) {
                LOGGER.info("Validating:"+targetIsoformRegion.getIsoformAccession()+",step2:isoform_not_found");
                continue;
            }
            String targetIsoformSequence = targetIsoform.getSequence().substring(begin - 1, end);

            // Calculates the matching score based on levenhstein distance
            String mappingSequence = query.getRegionSequence();
            float matchingScore = getMatchingScore(targetIsoformSequence, query.getRegionSequence());
            if(matchingScore >= 0.96) {
                LOGGER.info("Validating:"+isoform.getIsoformAccession()+",np_sequence:"+ targetIsoformSequence+",int_map_sequence:" + mappingSequence +",begin:" + begin + ",end:" + end + ",step2:matched_with_score_" + matchingScore);
                result.addMappedFeature(targetIsoform, begin, end);
            } else {
                LOGGER.info("Validating:"+isoform.getIsoformAccession()+",np_sequence:"+ targetIsoformSequence+",int_map_sequence:" + mappingSequence +",begin:" + begin + ",end:" + end + ",step2:rejected_with_score_" + matchingScore);
                continue;
            }
        }
        return result;
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
                LOGGER.info("propagating:"+isoform.getIsoformAccession()+",region:"+ region+",start:"+regionStart+",end:"+regionEnd+",region_length:"+region.length()+",target_isoform_accession:"+ targetIsoform.getIsoformAccession()+",target_isoform_accession:"+ targetIsoform.getIsoformAccession()+",target_isoform_start:"+targetIsoformRegionStart+",target_isoform_end:"+targetIsoformRegionEnd+",step2:rejected_no_projected_isoform_position");
                continue;
            } else {
                int projectedSequenceLegth = targetIsoformRegionEnd - targetIsoformRegionStart + 1;
                // Check if the sub sequence exists consecutively on the other isoform
                String targetIsoformRegion = targetIsoform.getSequence().substring(targetIsoformRegionStart - 1, targetIsoformRegionEnd);
                if(regionLength == projectedSequenceLegth) {
                    // If the sub sequence is shorter or equal to 30, an exact match is required
                    if(projectedSequenceLegth <= 30) {
                        boolean matched = region.equals(targetIsoformRegion);
                        LOGGER.info("propagating:"+isoform.getIsoformAccession()+",region:"+ region+",start:"+regionStart+",end:"+regionEnd+",region_length:"+region.length()+",target_isoform_accession:"+ targetIsoform.getIsoformAccession()+",target_isoform_region:"+targetIsoformRegion+",target_isoform_start:"+targetIsoformRegionStart+",target_isoform_end:"+targetIsoformRegionEnd+",target_isoform_region_length:"+targetIsoformRegion.length()+ ",step2:matched_"+matched);
                        if(matched) {
                            result.addMappedFeature(targetIsoform, targetIsoformRegionStart, targetIsoformRegionEnd);
                        } else {
                            continue;
                        }
                    } else {
                        // For sub sequences longer than 30 a mismatches are tolerated up to a level
                        float matchingScore = getMatchingScore(region,targetIsoformRegion);
                        if(matchingScore > 0.96) {
                            // Matching regions
                            LOGGER.info("propagating:"+isoform.getIsoformAccession()+",region:"+ region+",start:"+regionStart+",end:"+regionEnd+"region_length:"+region.length()+",target_isoform_accession:"+ targetIsoform.getIsoformAccession()+",target_isoform_region:"+targetIsoformRegion+",target_isoform_start:"+targetIsoformRegionStart+",target_isoform_end:"+targetIsoformRegionEnd+",target_isoform_region_length:"+targetIsoformRegion.length()+ ",step2:matched_with_score_"+matchingScore);
                            result.addMappedFeature(targetIsoform, targetIsoformRegionStart, targetIsoformRegionEnd);
                        } else {
                            LOGGER.info("propagating:"+isoform.getIsoformAccession()+",region:"+ region+",start:"+regionStart+",end:"+regionEnd+",region_length:"+region.length()+",target_isoform_accession:"+ targetIsoform.getIsoformAccession()+",target_isoform_region:"+targetIsoformRegion+",target_isoform_start:"+targetIsoformRegionStart+",target_isoform_end:"+targetIsoformRegionEnd+",target_isoform_region_length:"+targetIsoformRegion.length()+ ",step2:rejected_with_score_"+matchingScore);
                            continue;
                        }
                    }

                } else {
                    LOGGER.info("propagating:"+isoform.getIsoformAccession()+",region:"+ region+",start:"+regionStart+",end:"+regionEnd+",region_length:"+region.length()+",target_isoform_accession:"+ targetIsoform.getIsoformAccession()+",target_isoform_region:"+targetIsoformRegion+",target_isoform_start:"+targetIsoformRegionStart+",target_isoform_end:"+targetIsoformRegionEnd+",target_isoform_region_length:"+targetIsoformRegion.length()+ "step2:rejected_sequence_length_notequal");
                    continue;
                }
            }
        }

        return result;
    }


    /**
     * Calculates the edit distance based on levenshtein distance
     * @param seq1
     * @param seq2
     * @return Levenshtein distance
     */
    private float getMatchingScore(String seq1, String seq2) {
        LevenshteinDistance editDistanceCalculator = new LevenshteinDistance();
        int editDistance = editDistanceCalculator.apply(seq1, seq2);
        LOGGER.debug("Sequence1: " + seq1 + ",Sequence1_length:" + seq1.length() + ",Sequence2:" + seq2 + ",Sequence2_length:" + seq2.length()+",Levenshtein Distance:" + editDistance);
        int seqLength = seq2.length() > seq1.length() ? seq2.length() : seq1.length();
        return (float)(seqLength - editDistance)/seqLength;
    }
}

