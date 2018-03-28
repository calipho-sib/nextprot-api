package org.nextprot.api.core.service.impl;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.utils.StreamUtils;
import org.nextprot.api.core.dao.GeneDAO;
import org.nextprot.api.core.domain.*;
import org.nextprot.api.core.service.GenomicMappingService;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.utils.IsoformUtils;
import org.nextprot.api.core.utils.exon.ExonsAnalysisWithLogging;
import org.nextprot.api.core.utils.exon.GeneRegionMappingConflictSolver;
import org.nextprot.api.core.utils.exon.TranscriptExonsAnalyser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class GenomicMappingServiceImpl implements GenomicMappingService {

	// -Djava.util.logging.SimpleFormatter.format=%5$s%6$s%n
	private static final Logger LOGGER = Logger.getLogger(GenomicMappingServiceImpl.class.getSimpleName());

	@Autowired private GeneDAO geneDAO;
	@Autowired private IsoformService isoformService;
	private final Comparator<Isoform> isoformComparator = new IsoformUtils.IsoformComparator();

	@Override
	@Cacheable("genomic-mappings")
	public List<GenomicMapping> findGenomicMappingsByEntryName(String entryName) {

		Objects.requireNonNull(entryName, "The entry name "+entryName +" is not defined");
		Preconditions.checkArgument(!entryName.isEmpty(), "The entry name "+entryName +" is not empty");

		Map<Long, List<IsoformGeneMapping>> isoformGeneMappings = new IsoformGeneMappingsFinder(entryName)
				.find();

		List<GenomicMapping> genomicMappings = geneDAO.findGenomicMappingByEntryName(entryName).stream()
				.peek(genomicMapping -> {
					if (isoformGeneMappings.containsKey(genomicMapping.getGeneSeqId())) {
						genomicMapping.addAllIsoformGeneMappings(isoformGeneMappings.get(genomicMapping.getGeneSeqId()));
						genomicMapping.getIsoformGeneMappings().sort((im1, im2) -> isoformComparator.compare(im1.getIsoform(), im2.getIsoform()));
					}
				})
				.collect(Collectors.toList());

		return Collections.unmodifiableList(genomicMappings);
	}

	private class IsoformGeneMappingsFinder {

		private final Map<String, Isoform> isoformsByName;
		private final Map<String, List<IsoformGeneMapping>> isoformMappingsByIsoformName;
		private final Map<String, List<TranscriptGeneMapping>> transcriptGeneMappingsByIsoformName;

		private IsoformGeneMappingsFinder(String entryName) {

			this.isoformsByName = isoformService.findIsoformsByEntryName(entryName).stream().collect(Collectors.toMap(Isoform::getIsoformAccession, Function.identity()));
			this.isoformMappingsByIsoformName = geneDAO.getIsoformMappingsByIsoformName(isoformsByName.keySet());
			this.transcriptGeneMappingsByIsoformName = geneDAO.findTranscriptMappingsByIsoformName(isoformsByName.keySet());
		}

		Map<Long, List<IsoformGeneMapping>> find() {

			// Set missing fields of IsoformGeneMapping
			for (String isoformName : isoformMappingsByIsoformName.keySet()) {

				// By isoform name
				for (IsoformGeneMapping isoformGeneMapping : isoformMappingsByIsoformName.get(isoformName)) {

					isoformGeneMapping.setIsoform(isoformsByName.get(isoformName));

					// exons provided by ensembl can conflict isoform mappings and are solved here
					isoformGeneMapping.setTranscriptGeneMappings(StreamUtils.nullableListToStream(transcriptGeneMappingsByIsoformName.get(isoformName))
							.filter(tm -> tm.getReferenceGeneId() == isoformGeneMapping.getReferenceGeneId())
							.peek(tm -> resetTranscriptGeneMappingExons(tm, isoformGeneMapping.getIsoformGeneRegionMappings()))
							.sorted(Comparator.comparingInt(TranscriptGeneMapping::getNucleotideSequenceLength))
							.collect(Collectors.toList()));
				}
			}

			return isoformMappingsByIsoformName.values().stream()
					.flatMap(Collection::stream)
					.peek(isoformGeneMapping -> computeExonListPhasesAndAminoacids(isoformGeneMapping))
					.collect(Collectors.groupingBy(IsoformGeneMapping::getReferenceGeneId));
		}

		private void resetTranscriptGeneMappingExons(TranscriptGeneMapping transcriptGeneMapping, List<GeneRegion> isoformGeneMappings) {

			Map<Integer, Integer> exonsFromEnsemblIndices = new HashMap<>();

			List<GenericExon> exonsFromEnsembl = findExonsAlignedToTranscriptAccordingToEnsembl(
					transcriptGeneMapping.getIsoformName(),
					transcriptGeneMapping.getReferenceGeneUniqueName(),
					transcriptGeneMapping.getName(),
					transcriptGeneMapping.getQuality());

			List<GeneRegion> validatedGeneRegions = new GeneRegionMappingConflictSolver(
					exonsFromEnsembl.stream()
							.map(exon -> exon.getGeneRegion())
							.collect(Collectors.toList()), isoformGeneMappings
			).fixGeneRegions(exonsFromEnsemblIndices);

			transcriptGeneMapping.setExons(rebuildExonList(validatedGeneRegions, exonsFromEnsembl, exonsFromEnsemblIndices));
		}

		private List<GenericExon> findExonsAlignedToTranscriptAccordingToEnsembl(String isoformAccession, String refGeneUniqueName, String transcriptAccession, String quality) {

			List<GenericExon> exons;

			if ("GOLD".equalsIgnoreCase(quality)) {
				exons = geneDAO.findExonsAlignedToTranscriptOfGene(transcriptAccession, refGeneUniqueName);
			} else {
				exons = geneDAO.findExonsPartiallyAlignedToTranscriptOfGene(isoformAccession, transcriptAccession, refGeneUniqueName);
			}

			exons.sort(Comparator.comparingInt(Exon::getFirstPositionOnGene));

			return exons;
		}

		private void computeExonListPhasesAndAminoacids(IsoformGeneMapping isoformGeneMapping) {

			isoformGeneMapping.getTranscriptGeneMappings().forEach(transcriptMapping ->
					computeExonListPhasesAndAminoacids(transcriptMapping, isoformGeneMapping.getAminoAcidSequence(),
							isoformGeneMapping.getFirstPositionIsoformOnGene(),
							isoformGeneMapping.getLastPositionIsoformOnGene()));
		}

		private void computeExonListPhasesAndAminoacids(TranscriptGeneMapping transcriptGeneMapping, String bioSequence, int startPositionIsoformOnGene, int endPositionIsoformOnGene) {

			ExonsAnalysisWithLogging exonsAnalysisWithLogging = new ExonsAnalysisWithLogging();
			TranscriptExonsAnalyser analyser = new TranscriptExonsAnalyser(exonsAnalysisWithLogging);

			TranscriptExonsAnalyser.Results results = analyser.analyse(bioSequence, startPositionIsoformOnGene, endPositionIsoformOnGene,
					transcriptGeneMapping.getExons());

			if (!results.isSuccess()) {

				transcriptGeneMapping.setExons(results.getValidExons());

				LOGGER.severe("SKIPPING EXON(S) WITH MAPPING ERROR: isoform name=" + transcriptGeneMapping.getIsoformName() + ", transcript name=" + transcriptGeneMapping.getDatabaseAccession() + ", gene name=" + transcriptGeneMapping.getReferenceGeneUniqueName() + ", quality=" + transcriptGeneMapping.getQuality()
						+ ", exon structure=" + exonsAnalysisWithLogging.getMessage()+", messages="+results.getExceptionList().stream().map(e -> e.getMessage()).collect(Collectors.joining(",")));
			}
		}

		private List<GenericExon> rebuildExonList(List<GeneRegion> geneRegions, List<GenericExon> exonsFromEnsembl, Map<Integer, Integer> oldGeneRegionIndices) {

			List<GenericExon> exons = new ArrayList<>(geneRegions.size());

			for(int i = 0; i < geneRegions.size(); i++) {

				GenericExon exon;

				// already there
				if (oldGeneRegionIndices.containsKey(i)) {
					exon = exonsFromEnsembl.get(oldGeneRegionIndices.get(i));
				}
				else {
					exon = new GenericExon();
					exon.setGeneRegion(geneRegions.get(i));
					exon.setTranscriptName(exonsFromEnsembl.get(0).getTranscriptName());
				}

				exon.setRank(i + 1);

				exons.add(exon);
			}
			return exons;
		}

	}
}
