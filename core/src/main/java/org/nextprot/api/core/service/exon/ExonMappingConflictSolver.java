package org.nextprot.api.core.service.exon;

import org.nextprot.api.core.domain.GeneRegion;
import org.nextprot.api.core.domain.exon.SimpleExon;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class resolve the potential conflicts between GeneRegion mapped from transcript and from isoform
 */
public abstract class ExonMappingConflictSolver {

    protected final String isoformName;
    final List<SimpleExon> exonsFromEnsembl;
    final List<GeneRegion> transcriptToGeneMappings;
    final List<GeneRegion> isoformToGeneMappings;

    ExonMappingConflictSolver(String isoformName, List<SimpleExon> exonsFromEnsembl, List<GeneRegion> isoformToGeneMappings) {

        this.isoformName = isoformName;
        this.exonsFromEnsembl = exonsFromEnsembl;
        this.transcriptToGeneMappings = exonsFromEnsembl.stream()
                .map(exon -> exon.getGeneRegion())
                .collect(Collectors.toList());
        this.isoformToGeneMappings = isoformToGeneMappings;
    }

    public static ExonMappingConflictSolver newConflictSolver(String isoformName, List<SimpleExon> exonsFromEnsembl, List<GeneRegion> isoformToGeneMappings, boolean lowQualityMappings) {

        if (lowQualityMappings) {
            return new LowQualityGeneRegionMappingConflictSolver(isoformName, exonsFromEnsembl, isoformToGeneMappings);
        }
        return new GeneRegionMappingConflictSolver(isoformName, exonsFromEnsembl, isoformToGeneMappings);
    }

    protected abstract List<GeneRegion> resolveConflicts();
    protected abstract boolean foundGeneRegion(List<GeneRegion> validatedGeneRegions, int geneRegionIndex);
    protected abstract SimpleExon getEnsemblExon(List<GeneRegion> validatedGeneRegions, int geneRegionIndex);

    /**
     * Build the list of exon composed of ensembl exons and our mapping exons
     */
    public List<SimpleExon> solveMapping() {

        List<GeneRegion> validatedGeneRegions = resolveConflicts();

        List<SimpleExon> exons = new ArrayList<>(validatedGeneRegions.size());

        for(int i = 0; i < validatedGeneRegions.size(); i++) {

            SimpleExon exon;

            if (foundGeneRegion(validatedGeneRegions, i)) {
                exon = getEnsemblExon(validatedGeneRegions, i);
            }
            else {
                exon = new SimpleExon();
                exon.setGeneRegion(validatedGeneRegions.get(i));
                exon.setTranscriptName(exonsFromEnsembl.get(0).getTranscriptName());
            }

            exon.setIsoformName(isoformName);
            exon.setRank(i + 1);

            exons.add(exon);
        }
        return exons;
    }
}
