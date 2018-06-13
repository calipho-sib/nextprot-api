package org.nextprot.api.core.service.exon;

import org.nextprot.api.core.domain.GeneRegion;
import org.nextprot.api.core.domain.exon.SimpleExon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


class GeneRegionMappingConflictSolver extends ExonMappingConflictSolver {

    private final Map<Integer, Integer> transcriptToGeneMappingsIndices = new HashMap<>();

    GeneRegionMappingConflictSolver(String isoformName, List<SimpleExon> exonsFromEnsembl, List<GeneRegion> isoformToGeneMappings) {

        super(isoformName, exonsFromEnsembl, isoformToGeneMappings);
    }

    @Override
    protected List<GeneRegion> resolveConflicts() {

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

    @Override
    protected boolean foundGeneRegion(List<GeneRegion> validatedGeneRegions,int geneRegionIndex) {

        return transcriptToGeneMappingsIndices.containsKey(geneRegionIndex);
    }

    @Override
    protected SimpleExon getEnsemblExon(List<GeneRegion> validatedGeneRegions,int geneRegionIndex) {

        return exonsFromEnsembl.get(transcriptToGeneMappingsIndices.get(geneRegionIndex));
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
