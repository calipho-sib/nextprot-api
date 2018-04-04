package org.nextprot.api.core.service.exon;

import org.nextprot.api.core.domain.GeneRegion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class resolve the potential conflicts between GeneRegion mapped from transcript and from isoform
 */
public class GeneRegionMappingConflictSolver {

    private final List<GeneRegion> transcriptToGeneMappings;
    private final List<GeneRegion> isoformToGeneMappings;

    public GeneRegionMappingConflictSolver(List<GeneRegion> transcriptToGeneMappings, List<GeneRegion> isoformToGeneMappings) {

        this.transcriptToGeneMappings = transcriptToGeneMappings;
        this.isoformToGeneMappings = isoformToGeneMappings;
    }

    public List<GeneRegion> fixGeneRegions(Map<Integer, Integer> transcriptToGeneMappingsIndices) {

        List<GeneRegion> geneRegions = new ArrayList<>();

        int countSupplementaryExon = 0;

        for (int i=0 ; i<transcriptToGeneMappings.size() ; i++) {

            GeneRegion geneRegion = transcriptToGeneMappings.get(i);

            if (isCodingRegion(geneRegion)) {

                if (!foundIsoformGeneRegion(geneRegion)) {

                    List<GeneRegion> alternativeGeneRegions = findIncludedGeneRegionsInIsoformGeneRegion(geneRegion);

                    geneRegions.addAll(alternativeGeneRegions);

                    countSupplementaryExon += alternativeGeneRegions.size() - 1;
                    continue;
                }
            }
            geneRegions.add(geneRegion);
            transcriptToGeneMappingsIndices.put(i+countSupplementaryExon, i);
        }

        return geneRegions;
    }

    private boolean isCodingRegion(GeneRegion geneRegion) {

        return geneRegion.getFirstPosition() < lastCodingRegion().getLastPosition() &&
                geneRegion.getLastPosition() > isoformToGeneMappings.get(0).getFirstPosition();
    }

    private GeneRegion lastCodingRegion() {

        return isoformToGeneMappings.get(isoformToGeneMappings.size()-1);
    }

    private boolean foundIsoformGeneRegion(GeneRegion geneRegionMappedToTranscript) {

        for (int i=0 ; i<isoformToGeneMappings.size() ; i++) {

            if (isFirstCodingExon(i, geneRegionMappedToTranscript)  ||
                isLastCodingExon(i, geneRegionMappedToTranscript)   ||
                isSingleCodingExon(i, geneRegionMappedToTranscript) ||
                isInternalCodingExon(i, geneRegionMappedToTranscript)) {

                return true;
            }

            if (isFirstCodingExon(i, geneRegionMappedToTranscript)) {
                return true;
            }

        }
        return false;
    }

    private boolean isFirstCodingExon(int i, GeneRegion geneRegionMappedToTranscript) {

        GeneRegion isoformToGeneMapping = isoformToGeneMappings.get(i);

        return i == 0 && isoformToGeneMapping.getLastPosition() == geneRegionMappedToTranscript.getLastPosition() &&
                isoformToGeneMapping.getFirstPosition() >= geneRegionMappedToTranscript.getFirstPosition();
    }

    private boolean isLastCodingExon(int i, GeneRegion geneRegionMappedToTranscript) {

        GeneRegion isoformToGeneMapping = isoformToGeneMappings.get(i);

        return i == isoformToGeneMappings.size()-1 &&
                isoformToGeneMapping.getFirstPosition() == geneRegionMappedToTranscript.getFirstPosition()
                && isoformToGeneMapping.getLastPosition() <= geneRegionMappedToTranscript.getLastPosition();
    }

    private boolean isSingleCodingExon(int i, GeneRegion geneRegionMappedToTranscript) {

        GeneRegion isoformToGeneMapping = isoformToGeneMappings.get(i);

        return i == 0 && isoformToGeneMapping.getLastPosition() <= geneRegionMappedToTranscript.getLastPosition() &&
                isoformToGeneMapping.getFirstPosition() >= geneRegionMappedToTranscript.getFirstPosition();
    }

    private boolean isInternalCodingExon(int i, GeneRegion geneRegionMappedToTranscript) {

        GeneRegion isoformToGeneMapping = isoformToGeneMappings.get(i);

        return isoformToGeneMapping.getFirstPosition() == geneRegionMappedToTranscript.getFirstPosition() &&
                isoformToGeneMapping.getLastPosition() == geneRegionMappedToTranscript.getLastPosition();
    }

    private List<GeneRegion> findIncludedGeneRegionsInIsoformGeneRegion(GeneRegion geneRegion) {

        return isoformToGeneMappings.stream()
                .filter(gr -> gr.getFirstPosition() >= geneRegion.getFirstPosition() &&
                        gr.getLastPosition() <= geneRegion.getLastPosition())
                .collect(Collectors.toList());
    }
}
