package org.nextprot.api.core.service.impl;

import org.nextprot.api.core.domain.GeneRegion;
import org.nextprot.api.core.domain.GenomicMapping;
import org.nextprot.api.core.domain.exon.Exon;
import org.nextprot.api.core.domain.exon.ExonMapping;
import org.nextprot.api.core.service.EntryExonMappingService;
import org.nextprot.api.core.service.GenomicMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EntryExonMappingServiceImpl implements EntryExonMappingService {

	@Autowired
	private GenomicMappingService genomicMappingService;

	@Override
	public ExonMapping findExonMappingGeneXIsoformXShorterENST(String entryName) {

		ExonMapping mapping = new ExonMapping();
		Map<GeneRegion, Map<String, Exon>> exons = new HashMap<>();

		Optional<GenomicMapping> gm = findChosenIsoformGeneMapping(entryName);

		if (gm.isPresent()) {
			gm.get().getIsoformGeneMappings().stream()
					.peek(igm -> mapping.setIsoformInfos(igm.getIsoformAccession(),
							igm.getTranscriptGeneMappings().stream()
									.map(tgm -> tgm.getDatabaseAccession())
									.collect(Collectors.toList()),
							igm.getIsoformMainName()))
					.map(igm -> igm.getTranscriptGeneMappings().get(0).getExons())
					.flatMap(e -> e.stream())
					.forEach(exon -> {
						GeneRegion gr = exon.getGeneRegion();

						exons.computeIfAbsent(gr, k -> new HashMap<>())
								.put(exon.getIsoformName(), exon);
					});

			mapping.setExons(exons);
		}

		return mapping;
	}

	private Optional<GenomicMapping> findChosenIsoformGeneMapping(String entryName) {

		return genomicMappingService.findGenomicMappingsByEntryName(entryName).stream()
				.filter(genomicMapping -> genomicMapping.isChosenForAlignment())
				.findFirst();
	}

	/*
	@Override
	public List<GeneRegion> findSortedGeneRegionsOfRefENST(String entryName, String geneName) {

		return findSortedGeneRegions(entryName, entryName, transcriptGeneMappings -> transcriptGeneMappings.get(0));
	}

	@Override
	public List<GeneRegion> findSortedGeneRegions(String entryName, String geneName, Map<String, String> isoNameToENST) {

		return findSortedGeneRegions(entryName, entryName, transcriptGeneMappings -> transcriptGeneMappings.get(0));
	}

	private List<GeneRegion> findSortedGeneRegions(String entryName, String geneName, Function<List<TranscriptGeneMapping>, TranscriptGeneMapping> selector) {

		return findIsoformGeneMappings(entryName, geneName).stream()
				.map(igm -> igm.getTranscriptGeneMappings())
				.map(selector)
				.map(tgm -> tgm.getExons())
				.flatMap(e -> e.stream())
				.map(e -> e.getGeneRegion())
				.filter(distinctByKey(gr -> gr.getFirstPosition()+"."+gr.getLastPosition()))
				.sorted(Comparator.comparingInt(GeneRegion::getFirstPosition)
						.thenComparing((gr1, gr2) -> gr2.getLastPosition() - gr1.getLastPosition()))
				.collect(Collectors.toList());
	}

	// TODO: new feature to discuss with amos: instead of getting the exons mapping of the first ENST matching iso
	// it should be better imho to
	@Override
	public List<GeneRegion> findCommonGeneRegions(String entryName, String geneName, String isoformAccession) {

		Map<String, List<GeneRegion>> exonListByENST = findIsoformGeneMappings(entryName, geneName).stream()
				.map(igm -> igm.getTranscriptGeneMappings())
				.flatMap(tgm -> tgm.stream())
				.map(tgm -> tgm.getExons())
				.flatMap(e -> e.stream())
				.collect(Collectors.groupingBy(Exon::getTranscriptName, Collectors.mapping(
						e -> e.getGeneRegion(), Collectors.toList())));

		return new ArrayList<>(calcCommonGeneRegions(exonListByENST.values()));
	}

	private static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {

		Map<Object, Boolean> map = new ConcurrentHashMap<>();
		return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	public static Set<GeneRegion> calcCommonGeneRegions(Collection<List<GeneRegion>> exonsList) {

		Set<GeneRegion> common = new LinkedHashSet<>();
		if (!exonsList.isEmpty()) {
			Iterator<? extends Collection<GeneRegion>> iterator = exonsList.iterator();
			common.addAll(iterator.next());
			while (iterator.hasNext()) {
				common.retainAll(iterator.next());
			}
		}
		return common;
	}
	*/
}
