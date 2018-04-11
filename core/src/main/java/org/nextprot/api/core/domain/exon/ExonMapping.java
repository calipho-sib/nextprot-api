package org.nextprot.api.core.domain.exon;

import org.nextprot.api.core.domain.GeneRegion;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class ExonMapping implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<GeneRegion, Map<String, Exon>> exons = new HashMap<>();
    private List<String> sortedKeys = new ArrayList<>();
    private List<Map<String, Object>> isoformInfos = new ArrayList<>();

    public Map<GeneRegion, Map<String, Exon>> getExons() {
        return exons;
    }

    public void setExons(Map<GeneRegion, Map<String, Exon>> exons) {

        this.exons = exons;
        this.sortedKeys.addAll(new ArrayList<>(exons.keySet()).stream()
                        .sorted(Comparator.comparingInt(GeneRegion::getFirstPosition)
                                .thenComparing((gr1, gr2) -> gr2.getLastPosition() - gr1.getLastPosition()))
                        .map(gr -> gr.toString())
                        .collect(Collectors.toList()));
    }

    public List<Map<String, Object>> getIsoformInfos() {

        return Collections.unmodifiableList(isoformInfos);
    }

    public void setIsoformInfos(String isoformAccession, List<String> ensts, String mainName) {

        HashMap<String, Object> infos = new HashMap<>();

        infos.put("accession", isoformAccession);
        infos.put("name", mainName);
        infos.put("main-transcript", ensts.get(0));
        if (ensts.size() > 1) {
            infos.put("other-transcripts", ensts.subList(1, ensts.size()));
        }

        this.isoformInfos.add(infos);
    }

    public List<String> getSortedKeys() {

        return Collections.unmodifiableList(sortedKeys);
    }
}
