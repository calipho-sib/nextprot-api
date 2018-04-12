package org.nextprot.api.core.service.impl;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.utils.StreamUtils;
import org.nextprot.api.core.dao.GeneDAO;
import org.nextprot.api.core.domain.*;
import org.nextprot.api.core.domain.exon.SimpleExon;
import org.nextprot.api.core.service.GenomicMappingService;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.service.exon.ExonsAnalysisWithLogging;
import org.nextprot.api.core.service.exon.GeneRegionMappingConflictSolver;
import org.nextprot.api.core.service.exon.TranscriptExonsCategorizer;
import org.nextprot.api.core.utils.IsoformUtils;
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

		Map<String, Isoform> isoformsByName = isoformService.findIsoformsByEntryName(entryName).stream()
				.collect(Collectors.toMap(Isoform::getIsoformAccession, Function.identity()));

		Map<Long, List<IsoformGeneMapping>> isoformGeneMappings = new IsoformGeneMappingsFinder(isoformsByName)
				.find();

		List<GenomicMapping> genomicMappings = geneDAO.findGenomicMappingByEntryName(entryName).stream()
				.peek(genomicMapping -> {
					if (isoformGeneMappings.containsKey(genomicMapping.getGeneSeqId())) {
						genomicMapping.addAllIsoformGeneMappings(isoformGeneMappings.get(genomicMapping.getGeneSeqId()));
						genomicMapping.getIsoformGeneMappings().sort((im1, im2) -> isoformComparator.compare(isoformsByName.get(im1.getIsoformAccession()), isoformsByName.get(im2.getIsoformAccession())));
					}

					genomicMapping.setNonMappingIsoforms(calcNonMappingIsoformAccessionList(isoformsByName.keySet(),
							genomicMapping.getIsoformGeneMappings().stream()
									.map(igm -> igm.getIsoformAccession())
									.collect(Collectors.toList())));
				})
				.collect(Collectors.toList());

		return Collections.unmodifiableList(genomicMappings);
	}

	private List<String> calcNonMappingIsoformAccessionList(Set<String> allIsoformAccessions, List<String> mappedIsoformAccessions) {

		List<String> accessions = new ArrayList<>(allIsoformAccessions);

		accessions.removeAll(mappedIsoformAccessions);

		return accessions;
	}

	private class IsoformGeneMappingsFinder {

		private final Map<String, Isoform> isoformsByName;
		private final Map<String, List<IsoformGeneMapping>> isoformMappingsByIsoformName;
		private final Map<String, List<TranscriptGeneMapping>> transcriptGeneMappingsByIsoformName;

		private IsoformGeneMappingsFinder(Map<String, Isoform> isoformsByName) {

			this.isoformsByName = isoformsByName;
			this.isoformMappingsByIsoformName = geneDAO.getIsoformMappingsByIsoformName(isoformsByName.keySet());
			this.transcriptGeneMappingsByIsoformName = geneDAO.findTranscriptMappingsByIsoformName(isoformsByName.keySet());
		}

		Map<Long, List<IsoformGeneMapping>> find() {

			// key=gene.isoform.transcript
			Map<String, List<SimpleExon>> uncategorizedExons = new HashMap<>();

			// Set missing fields of IsoformGeneMapping
			for (String isoformName : isoformMappingsByIsoformName.keySet()) {

				// By isoform name
				for (IsoformGeneMapping isoformGeneMapping : isoformMappingsByIsoformName.get(isoformName)) {

				    isoformGeneMapping.setIsoformMainName(isoformsByName.get(isoformName).getMainEntityName().getName());

					// exons provided by ensembl can conflict with isoform to gene mappings and are solved here
					isoformGeneMapping.setTranscriptGeneMappings(StreamUtils.nullableListToStream(transcriptGeneMappingsByIsoformName.get(isoformName))
							.filter(tm -> tm.getReferenceGeneId() == isoformGeneMapping.getReferenceGeneId())
							.peek(tm -> uncategorizedExons.put(buildKey(tm),
									buildExonListFromTranscriptAndIsoformGeneMapping(tm, isoformGeneMapping.getIsoformGeneRegionMappings())))
							.sorted(Comparator.comparingInt(TranscriptGeneMapping::getNucleotideSequenceLength))
							.collect(Collectors.toList()));
				}
			}

			return isoformMappingsByIsoformName.values().stream()
					.flatMap(Collection::stream)
					.peek(isoformGeneMapping -> computeExonListPhasesAndAminoacids(uncategorizedExons, isoformGeneMapping))
					.collect(Collectors.groupingBy(IsoformGeneMapping::getReferenceGeneId));
		}

		private String buildKey(TranscriptGeneMapping transcriptGeneMapping) {

			return transcriptGeneMapping.getReferenceGeneUniqueName()+"."+transcriptGeneMapping.getIsoformName()+"."+transcriptGeneMapping.getName();
		}

		private List<SimpleExon> buildExonListFromTranscriptAndIsoformGeneMapping(TranscriptGeneMapping transcriptGeneMapping, List<GeneRegion> isoformGeneMappings) {

			Map<Integer, Integer> transcriptToGeneMappingsIndices = new HashMap<>();

			List<SimpleExon> exonsFromEnsembl = findExonsAlignedToTranscriptAccordingToEnsembl(
					transcriptGeneMapping.getIsoformName(),
					transcriptGeneMapping.getReferenceGeneUniqueName(),
					transcriptGeneMapping.getName(),
					transcriptGeneMapping.getQuality());

			List<GeneRegion> validatedGeneRegions = new GeneRegionMappingConflictSolver(
					exonsFromEnsembl.stream()
							.map(exon -> exon.getGeneRegion())
							.collect(Collectors.toList()), isoformGeneMappings
			).fixGeneRegions(transcriptToGeneMappingsIndices);

			return buildExonList(validatedGeneRegions, exonsFromEnsembl, transcriptToGeneMappingsIndices, transcriptGeneMapping.getIsoformName());
		}

		private List<SimpleExon> findExonsAlignedToTranscriptAccordingToEnsembl(String isoformAccession, String refGeneUniqueName, String transcriptAccession, String quality) {

			List<SimpleExon> exons;

			if ("GOLD".equalsIgnoreCase(quality)) {
				exons = geneDAO.findExonsAlignedToTranscriptOfGene(transcriptAccession, refGeneUniqueName);
			} else {
				exons = geneDAO.findExonsPartiallyAlignedToTranscriptOfGene(isoformAccession, transcriptAccession, refGeneUniqueName);
			}

			return exons;
		}

		private void computeExonListPhasesAndAminoacids(Map<String, List<SimpleExon>> exons, IsoformGeneMapping isoformGeneMapping) {

			isoformGeneMapping.getTranscriptGeneMappings().forEach(transcriptMapping ->
					computeExonListPhasesAndAminoacids(exons.get(buildKey(transcriptMapping)),
							transcriptMapping, isoformsByName.get(transcriptMapping.getIsoformName()).getSequence(),
							isoformGeneMapping.getFirstPositionIsoformOnGene(),
							isoformGeneMapping.getLastPositionIsoformOnGene()));
		}

		private void computeExonListPhasesAndAminoacids(List<SimpleExon> exons, TranscriptGeneMapping transcriptGeneMapping, String bioSequence, int startPositionIsoformOnGene, int endPositionIsoformOnGene) {

			ExonsAnalysisWithLogging exonsAnalysisWithLogging = new ExonsAnalysisWithLogging();
			TranscriptExonsCategorizer analyser = new TranscriptExonsCategorizer(exonsAnalysisWithLogging);

			TranscriptExonsCategorizer.Results results = analyser.categorizeExons(exons, bioSequence, startPositionIsoformOnGene, endPositionIsoformOnGene);
			transcriptGeneMapping.setExons(results.getCategorizedExons());

			if (results.hasMappingErrors()) {

				LOGGER.severe("SKIPPING EXON(S) WITH MAPPING ERROR: isoform name=" + transcriptGeneMapping.getIsoformName() + ", transcript name=" + transcriptGeneMapping.getDatabaseAccession() + ", gene name=" + transcriptGeneMapping.getReferenceGeneUniqueName() + ", quality=" + transcriptGeneMapping.getQuality()
						+ ", exon structure=" + exonsAnalysisWithLogging.getMessage()+", messages="+results.getExceptionList().stream().map(e -> e.getMessage()).collect(Collectors.joining(",")));
			}
		}

		/**
		 * Build the list of exon composed of ensembl exons and our mapping exons
		 */
		private List<SimpleExon> buildExonList(List<GeneRegion> geneRegions, List<SimpleExon> exonsFromEnsembl, Map<Integer, Integer> transcriptToGeneMappingsIndices, String isoformName) {

			List<SimpleExon> exons = new ArrayList<>(geneRegions.size());

			for(int i = 0; i < geneRegions.size(); i++) {

				SimpleExon exon;

				if (transcriptToGeneMappingsIndices.containsKey(i)) {
					exon = exonsFromEnsembl.get(transcriptToGeneMappingsIndices.get(i));
				}
				else {
					exon = new SimpleExon();
					exon.setGeneRegion(geneRegions.get(i));
					exon.setTranscriptName(exonsFromEnsembl.get(0).getTranscriptName());
				}

				exon.setIsoformName(isoformName);
				exon.setRank(i + 1);

				exons.add(exon);
			}
			return exons;
		}

	}
}
