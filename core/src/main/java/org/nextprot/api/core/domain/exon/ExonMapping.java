package org.nextprot.api.core.domain.exon;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.GeneRegion;
import org.nextprot.api.core.utils.IsoformUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class ExonMapping implements Serializable {

    private static final long serialVersionUID = 7L;

    private Map<GeneRegion, Map<String, CategorizedExon>> exons = new HashMap<>();
    private List<String> sortedExonKeys = new ArrayList<>();
    private List<String> sortedMappedIsoformInfoKeys = new ArrayList<>();
    private Map<String, Map<String, Object>> mappedIsoformInfos = new HashMap<>();
    private List<String> nonMappedIsoforms = new ArrayList<>();
    private List<Integer> startExonPositions = new ArrayList<>();
    private List<Integer> stopExonPositions = new ArrayList<>();
    private boolean lowQualityMappings;

    public Map<GeneRegion, Map<String, CategorizedExon>> getExons() {
        return exons;
    }

    public void setExons(Map<GeneRegion, Map<String, CategorizedExon>> exons) {

        this.exons = exons;
        this.sortedExonKeys.addAll(new ArrayList<>(exons.keySet()).stream()
                        .sorted(Comparator.comparingInt(GeneRegion::getFirstPosition)
                                .thenComparingInt(GeneRegion::getLastPosition))
                        .map(gr -> gr.toString())
                        .collect(Collectors.toList()));

        this.startExonPositions = extractStartExonPositions();
        this.stopExonPositions = extractStopExonPositions();
    }

    public void calcSortedMappedIsoformKeys(String canonicalIsoformAccession) {

        sortedMappedIsoformInfoKeys = updateSortedMappedIsoformKeys(canonicalIsoformAccession);
    }

    private List<Integer> extractStartExonPositions() {

        return exons.values().stream()
                .map(m -> m.values())
                .flatMap(m -> m.stream())
                .filter(e -> e.getExonCategory() == ExonCategory.START || e.getExonCategory() == ExonCategory.MONO)
                .map(e -> ((FirstCodingExon) e).getStartPosition())
                .sorted()
                .distinct()
                .collect(Collectors.toList());
    }

    private List<Integer> extractStopExonPositions() {

        return exons.values().stream()
                .map(m -> m.values())
                .flatMap(m -> m.stream())
                .filter(e -> e.getExonCategory() == ExonCategory.STOP || e.getExonCategory() == ExonCategory.MONO)
                .map(e -> ((LastCodingExon) e).getStopPosition())
                .sorted(Comparator.reverseOrder())
                .distinct()
                .collect(Collectors.toList());
    }

    public Map<String, Map<String, Object>> getMappedIsoformInfos() {

        return Collections.unmodifiableMap(mappedIsoformInfos);
    }

    public void setIsoformInfos(String isoformAccession, List<String> ensts, String mainName, String quality) {

        if (this.mappedIsoformInfos.containsKey(isoformAccession)) {

            throw new NextProtException("infos already exist for isoform "+isoformAccession);
        }

        this.mappedIsoformInfos.put(isoformAccession, new HashMap<>());

        Map<String, Object> infos = mappedIsoformInfos.get(isoformAccession);

        infos.put("accession", isoformAccession);
        infos.put("name", mainName);
        infos.put("main-transcript", ensts.get(0));
        if (ensts.size() > 1) {
            infos.put("other-transcripts", ensts.subList(1, ensts.size()));
        }
        infos.put("quality", quality);
    }

    public List<String> getSortedExonKeys() {

        return Collections.unmodifiableList(sortedExonKeys);
    }

    private List<String> updateSortedMappedIsoformKeys(String canonicalIsoformAccession) {

        List<String> mappedIsoform = new ArrayList<>();

        if (mappedIsoformInfos.containsKey(canonicalIsoformAccession)) {
            mappedIsoform.add(canonicalIsoformAccession);
        }

        //CANONICAL first then list in order based on accession numeric values
        mappedIsoformInfos.keySet().stream()
                .filter(isoAccession -> !isoAccession.equals(canonicalIsoformAccession))
                .sorted(new IsoformUtils.ByIsoformUniqueNameComparator())
                .forEach(isoformAccession -> mappedIsoform.add(isoformAccession));

        return mappedIsoform;
    }

    public List<String> getSortedMappedIsoformInfoKeys() {

        return Collections.unmodifiableList(sortedMappedIsoformInfoKeys);
    }

    public List<String> getNonMappedIsoforms() {
        return nonMappedIsoforms;
    }

    public void setNonMappedIsoforms(List<String> nonMappedIsoforms) {
        this.nonMappedIsoforms = nonMappedIsoforms.stream()
                .sorted(new IsoformUtils.ByIsoformUniqueNameComparator())
                .collect(Collectors.toList());
    }

    public List<Integer> getStartExonPositions() {
        return startExonPositions;
    }

    public List<Integer> getStopExonPositions() {
        return stopExonPositions;
    }

    public boolean isLowQualityMappings() {
        return lowQualityMappings;
    }

    public void setLowQualityMappings(boolean lowQualityMappings) {
        this.lowQualityMappings = lowQualityMappings;
    }
}
