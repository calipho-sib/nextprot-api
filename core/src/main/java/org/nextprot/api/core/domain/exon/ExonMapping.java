package org.nextprot.api.core.domain.exon;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.GeneRegion;
import org.nextprot.api.core.utils.IsoformUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class ExonMapping implements Serializable {

    private static final long serialVersionUID = 2L;

    private Map<GeneRegion, Map<String, Exon>> exons = new HashMap<>();
    private List<String> sortedExonKeys = new ArrayList<>();
    private Map<String, Map<String, Object>> isoformInfos = new HashMap<>();
    private List<String> nonAlignedIsoforms = new ArrayList<>();

    public Map<GeneRegion, Map<String, Exon>> getExons() {
        return exons;
    }

    public void setExons(Map<GeneRegion, Map<String, Exon>> exons) {

        this.exons = exons;
        this.sortedExonKeys.addAll(new ArrayList<>(exons.keySet()).stream()
                        .sorted(Comparator.comparingInt(GeneRegion::getFirstPosition)
                                .thenComparing((gr1, gr2) -> gr2.getLastPosition() - gr1.getLastPosition()))
                        .map(gr -> gr.toString())
                        .collect(Collectors.toList()));
    }

    public Map<String, Map<String, Object>> getIsoformInfos() {

        return Collections.unmodifiableMap(isoformInfos);
    }

    public void setIsoformInfos(String isoformAccession, List<String> ensts, String mainName) {

        if (this.isoformInfos.containsKey(isoformAccession)) {

            throw new NextProtException("infos already exist for isoform "+isoformAccession);
        }

        this.isoformInfos.put(isoformAccession, new HashMap<>());

        Map<String, Object> infos = isoformInfos.get(isoformAccession);

        infos.put("accession", isoformAccession);
        infos.put("name", mainName);
        infos.put("main-transcript", ensts.get(0));
        if (ensts.size() > 1) {
            infos.put("other-transcripts", ensts.subList(1, ensts.size()));
        }
    }

    public List<String> getSortedExonKeys() {

        return Collections.unmodifiableList(sortedExonKeys);
    }

    public List<String> getSortedIsoformKeys() {

        return Collections.unmodifiableList(isoformInfos.keySet().stream()
                .sorted(new IsoformUtils.ByIsoformUniqueNameComparator())
                .collect(Collectors.toList()));
    }

    public List<String> getNonAlignedIsoforms() {
        return nonAlignedIsoforms;
    }

    public void setNonAlignedIsoforms(List<String> nonAlignedIsoforms) {
        this.nonAlignedIsoforms = nonAlignedIsoforms;
    }
}
