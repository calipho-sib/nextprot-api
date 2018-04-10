package org.nextprot.api.core.domain.exon;

import org.nextprot.api.core.domain.GeneRegion;

import java.io.Serializable;
import java.util.*;

public class ExonMapping implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<GeneRegion, Map<String, Exon>> exons = new HashMap<>();
    private List<GeneRegion> sortedGeneRegions = new ArrayList<>();
    private Map<String, Map<String, Object>> isoformInfos = new HashMap<>();

    public Map<GeneRegion, Map<String, Exon>> getExons() {
        return exons;
    }

    public void setExons(Map<GeneRegion, Map<String, Exon>> exons) {

        this.exons = exons;
        this.sortedGeneRegions.addAll(exons.keySet());
        this.sortedGeneRegions.sort(Comparator.comparingInt(GeneRegion::getFirstPosition)
                .thenComparing((gr1, gr2) -> gr2.getLastPosition() - gr1.getLastPosition()));
    }

    public Map<String, Map<String, Object>> getIsoformInfos() {

        return Collections.unmodifiableMap(isoformInfos);
    }

    public void setIsoformInfos(String isoformName, List<String> ensts, String mainName) {

        this.isoformInfos.computeIfAbsent(isoformName, k -> new HashMap<>())
                .put("transcripts", ensts);
        this.isoformInfos.get(isoformName).put("name", mainName);
    }

    public List<GeneRegion> getSortedGeneRegions() {

        return Collections.unmodifiableList(sortedGeneRegions);
    }
}
