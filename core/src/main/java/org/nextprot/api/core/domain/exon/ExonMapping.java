package org.nextprot.api.core.domain.exon;

import org.nextprot.api.core.domain.GeneRegion;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ExonMapping implements Serializable {

    private static final long serialVersionUID = 1L;

    private GeneRegion geneRegion;

    private Map<String, Map<String, CategorizedExon>> exons = new HashMap<>();

    public GeneRegion getGeneRegion() {
        return geneRegion;
    }

    public void setGeneRegion(GeneRegion geneRegion) {
        this.geneRegion = geneRegion;
    }

    public void addExon(CategorizedExon exon, String isoformMainName, String ensemblTranscriptAccession) {

        exons.computeIfAbsent(isoformMainName, k -> new HashMap<>())
                .put(ensemblTranscriptAccession, exon);
    }

    public Map<String, Map<String, CategorizedExon>> getExonsByIsoformByENST() {
        return exons;
    }
}
