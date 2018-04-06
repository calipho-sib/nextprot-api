package org.nextprot.api.core.service.exon;

import org.nextprot.api.commons.app.SpringConfig;
import org.nextprot.api.core.domain.GeneRegion;
import org.nextprot.api.core.domain.GenomicMapping;
import org.nextprot.api.core.domain.IsoformGeneMapping;
import org.nextprot.api.core.domain.TranscriptGeneMapping;
import org.nextprot.api.core.service.GenomicMappingService;

import java.util.*;
import java.util.stream.Collectors;

public class ExonsMappingConsistency {

    private final String isoformName;
    private final SpringConfig springConfig;

    public ExonsMappingConsistency(String isoformName) {

        this.isoformName = isoformName;
        this.springConfig = new SpringConfig("dev");
    }

    public ConsistencyResult check(String ensgAccession) {

        springConfig.startApplicationContext();

        GenomicMappingService genomicMappingService = springConfig.getBean(GenomicMappingService.class);

        Optional<GenomicMapping> genomicMapping = genomicMappingService.findGenomicMappingsByEntryName(isoformName.split("-")[0]).stream()
                .filter(gm -> gm.getAccession().equals(ensgAccession))
                .findFirst();

        ConsistencyResult consistencyResult = new ConsistencyResult();

        genomicMapping.ifPresent(genomicMapping1 -> check(genomicMapping1, consistencyResult));

        springConfig.stopApplicationContext();

        return consistencyResult;
    }

    public void check(GenomicMapping genomicMapping, ConsistencyResult consistencyResult) {

        List<IsoformGeneMapping> iml = genomicMapping.getIsoformGeneMappings();

        Optional<IsoformGeneMapping> isoformMapping = iml.stream()
                .filter(im -> im.getIsoformAccession().equals(isoformName))
                .findFirst();

        isoformMapping.ifPresent(im -> compareTranscriptMappingsWithIsoformPosOnRefGene(im, consistencyResult));
    }

    private void compareTranscriptMappingsWithIsoformPosOnRefGene(IsoformGeneMapping isoformGeneMapping, ConsistencyResult consistencyResult) {

        List<GeneRegion> isoPositionsOnRefGene = isoformGeneMapping.getIsoformGeneRegionMappings();

        for (TranscriptGeneMapping tm : isoformGeneMapping.getTranscriptGeneMappings()) {

            List<GeneRegion> geneRegions = tm.getExons().stream()
                    .map(exon -> exon.getGeneRegion())
                    .filter(gi -> gi.getLastPosition() > isoPositionsOnRefGene.get(0).getFirstPosition())
                    .collect(Collectors.toList());

            compareTranscriptMappingWithIsoformPosOnRefGene(tm.getName(), isoPositionsOnRefGene, geneRegions, consistencyResult);
        }
    }

    private void compareTranscriptMappingWithIsoformPosOnRefGene(String enst, List<GeneRegion> isoPositionsOnRefGene,
                                                                 List<GeneRegion> exonsPositions, ConsistencyResult consistencyResult) {

        if (isoPositionsOnRefGene.size() != exonsPositions.size()) {

            consistencyResult.addInconsistency(enst, "different number of exons: isoPosOnRef="+formatPositions(isoPositionsOnRefGene)+ ", exonsPosOnRef="+formatPositions(exonsPositions));
        }

        for (GeneRegion exonPos : exonsPositions) {

            // search the position iso/generef matching exonPos
            int index = indexOfIsoPositionsOnRefGene(exonPos.getFirstPosition(), exonPos.getLastPosition(), isoPositionsOnRefGene);

            if (index < 0) {
                consistencyResult.addInconsistency(enst, "cannot find exon ["+exonPos.getFirstPosition()+"-"+exonPos.getLastPosition()+"] in "+formatPositions(isoPositionsOnRefGene));
            }
        }

    }

    private int indexOfIsoPositionsOnRefGene(int exonFrom, int exonTo, List<GeneRegion> isoPositionsOnRefGene) {

        for (int i=0 ; i<isoPositionsOnRefGene.size() ; i++) {

            GeneRegion pos = isoPositionsOnRefGene.get(i);

            // first or last exon
            if (i == 0 && pos.getLastPosition() == exonTo && pos.getFirstPosition() >= exonFrom ||
                i == isoPositionsOnRefGene.size()-1 && pos.getFirstPosition() == exonFrom && pos.getLastPosition() <= exonTo ||
                    pos.getFirstPosition() == exonFrom && pos.getLastPosition() == exonTo) {

                return i;
            }
        }
        return -1;
    }

    private String formatPositions(List<GeneRegion> positions) {

        return positions.stream()
                .map(e -> e.getFirstPosition()+"-"+e.getLastPosition())
                .collect(Collectors.joining(","));
    }

    public static class ConsistencyResult {

        private final Map<String, List<String>> errorMessagesByEnst = new HashMap<>();

        public void addInconsistency(String enst, String message) {

            errorMessagesByEnst.putIfAbsent(enst, new ArrayList<>());
            errorMessagesByEnst.get(enst).add(message);
        }

        public Map<String, List<String>> getErrorMessagesByEnst() {
            return Collections.unmodifiableMap(errorMessagesByEnst);
        }

        public boolean isConsistent() {
            return errorMessagesByEnst.isEmpty();
        }
    }
}
