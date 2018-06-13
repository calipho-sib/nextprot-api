package org.nextprot.api.core.service.exon;

import org.nextprot.api.core.domain.GeneRegion;
import org.nextprot.api.core.domain.exon.SimpleExon;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

class LowQualityGeneRegionMappingConflictSolver extends ExonMappingConflictSolver {

    private final Map<GeneRegion, SimpleExon> geneRegionToEnsemblExon;

    LowQualityGeneRegionMappingConflictSolver(String isoformName, List<SimpleExon> exonsFromEnsembl, List<GeneRegion> isoformToGeneMappings) {

        super(isoformName, exonsFromEnsembl, isoformToGeneMappings);

        this.geneRegionToEnsemblExon = exonsFromEnsembl.stream()
                .collect(Collectors.toMap(SimpleExon::getGeneRegion, Function.identity()));
    }

    /**
     * Anytime an isoform to gene region does not map the same entire transcript to gene region, we keep
     *
     * Case 1:
     * GENE        -------[         ]---------
     * TRANSCRIPT  -------[         ]---------
     * ISOFORM     -------[   ]--[  ]--------- those mappings should be chosen instead of the mapping above
     *
     * @return
     */
    @Override
    protected List<GeneRegion> resolveConflicts() {

        return buildTranscriptToIsoMappings().values().stream()
                .flatMap(Collection::stream)
                .sorted(Comparator.comparingInt(GeneRegion::getFirstPosition)
                        .thenComparingInt(GeneRegion::getLastPosition))
                .collect(Collectors.toList());
    }

    @Override
    protected boolean foundGeneRegion(List<GeneRegion> validatedGeneRegions, int geneRegionIndex) {

        return geneRegionToEnsemblExon.containsKey(validatedGeneRegions.get(geneRegionIndex));
    }

    @Override
    protected SimpleExon getEnsemblExon(List<GeneRegion> validatedGeneRegions, int geneRegionIndex) {

        return geneRegionToEnsemblExon.get(validatedGeneRegions.get(geneRegionIndex));
    }

    private Map<GeneRegion, List<GeneRegion>> buildTranscriptToIsoMappings() {

        Map<GeneRegion, List<GeneRegion>> transcriptToIsoMappings = new HashMap<>();

        for (int i=0 ; i<transcriptToGeneMappings.size() ; i++) {

            GeneRegion transcriptToGeneRegion = transcriptToGeneMappings.get(i);

            List<GeneRegion> mappingIsoRegions = new ArrayList<>();

            transcriptToIsoMappings.put(transcriptToGeneRegion, mappingIsoRegions);

            for (int j=0 ; j<isoformToGeneMappings.size() ; j++) {

                GeneRegion isoToGeneRegion = isoformToGeneMappings.get(j);

                // isoform sequence region is included in the transcript region
                /*
                TRANSCRIPT  -------[    1     ]---------
                ISOFORM     ---------[ A ]-[B]----------
                => 1 -> [A, B]
                 */
                if (isoToGeneRegion.getFirstPosition() >= transcriptToGeneRegion.getFirstPosition() &&
                        isoToGeneRegion.getLastPosition() <= transcriptToGeneRegion.getLastPosition()) {

                    mappingIsoRegions.add(isoToGeneRegion);
                }
            }

            // non coding region
            if (mappingIsoRegions.isEmpty()) {

                mappingIsoRegions.add(transcriptToGeneRegion);
            }
        }

        return transcriptToIsoMappings;
    }
}
