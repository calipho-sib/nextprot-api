package org.nextprot.api.core.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.nextprot.api.commons.utils.StreamUtils;
import org.nextprot.api.core.dao.GeneDAO;
import org.nextprot.api.core.domain.*;
import org.nextprot.api.core.service.GenomicMappingService;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.utils.IsoformUtils;
import org.nextprot.api.core.utils.exon.ExonsAnalysisMessageBuilder;
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

		List<GenomicMapping> genomicMappings = geneDAO.findGenomicMappingByEntryName(entryName);
		Collection<IsoformGeneMapping> isoformGeneMappings = findIsoformGeneMappings(entryName);

		isoformGeneMappings.forEach(isoformMapping -> analyseExons(isoformMapping));

		// Puts the isoformMappings to the associated gene and order it
		for (GenomicMapping genomicMapping : genomicMappings) {

			List<IsoformGeneMapping> mappings = isoformGeneMappings.stream()
					.filter(im -> im.getReferenceGeneId() == genomicMapping.getGeneSeqId())
					.collect(Collectors.toList());

			genomicMapping.addAllIsoformMappings(mappings);
			genomicMapping.getIsoformGeneMappings().sort((im1, im2) -> isoformComparator.compare(im1.getIsoform(), im2.getIsoform()));
		}

		return new ImmutableList.Builder<GenomicMapping>().addAll(genomicMappings).build();
	}

	private Collection<IsoformGeneMapping> findIsoformGeneMappings(String entryName) {

		Map<String, Isoform> isoformsByName = findIsoforms(entryName).stream()
				.collect(Collectors.toMap(Isoform::getIsoformAccession, Function.identity()));

        Map<String, List<IsoformGeneMapping>> isoformMappingsByIsoformName = geneDAO.getIsoformMappingsByIsoformName(isoformsByName.keySet());
        Map<String, List<TranscriptGeneMapping>> transcriptMappingsByIsoformName = geneDAO.findTranscriptMappingsByIsoformName(isoformsByName.keySet());

		// Set missing fields of IsoformMappings
		for (String isoformName : isoformMappingsByIsoformName.keySet()) {

		    for (IsoformGeneMapping isoformGeneMapping : isoformMappingsByIsoformName.get(isoformName)) {

				isoformGeneMapping.setIsoform(isoformsByName.get(isoformName));

				// filter transcript mappings by gene name
				List<TranscriptGeneMapping> transcriptGeneMappings = StreamUtils.nullableListToStream(transcriptMappingsByIsoformName.get(isoformName))
						.filter(tm -> tm.getReferenceGeneId() == isoformGeneMapping.getReferenceGeneId())
						.collect(Collectors.toList());

				for (TranscriptGeneMapping transcriptGeneMapping : transcriptGeneMappings) {

					transcriptGeneMapping.setExons(
							findExonsAlignedToTranscriptAccordingToEnsembl(
									isoformName,
									transcriptGeneMapping.getReferenceGeneUniqueName(),
									transcriptGeneMapping.getUniqueName(),
									transcriptGeneMapping.getQuality()));
				}

				isoformGeneMapping.setTranscriptGeneMappings(transcriptGeneMappings);
			}
		}

		return isoformMappingsByIsoformName.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
	}

	private void analyseExons(IsoformGeneMapping isoformGeneMapping) {

		isoformGeneMapping.getTranscriptGeneMappings().forEach(transcriptMapping ->
			computeExonListPhasesAndAminoacids(transcriptMapping, isoformGeneMapping.getAminoAcidSequence(),
						isoformGeneMapping.getFirstPositionIsoformOnGene(),
						isoformGeneMapping.getLastPositionIsoformOnGene()));
	}

	private void computeExonListPhasesAndAminoacids(TranscriptGeneMapping transcriptGeneMapping, String bioSequence, int startPositionIsoformOnGene, int endPositionIsoformOnGene) {

		TranscriptExonsAnalyser analyser;
		ExonsAnalysisMessageBuilder exonsAnalysisMessageBuilder = new ExonsAnalysisMessageBuilder();
        analyser = new TranscriptExonsAnalyser(exonsAnalysisMessageBuilder);

        boolean success = analyser.analyse(bioSequence, startPositionIsoformOnGene, endPositionIsoformOnGene,
				transcriptGeneMapping.getExons());

        if (!success) {
			LOGGER.severe("MAPPING ERROR: " + transcriptGeneMapping.getIsoformName() + "." + transcriptGeneMapping.getDatabaseAccession() + "." + transcriptGeneMapping.getReferenceGeneUniqueName() + " (" + transcriptGeneMapping.getQuality() + "): " + exonsAnalysisMessageBuilder.getMessage());
		}
	}

	private List<Isoform> findIsoforms(String entryName) {

		List<Isoform> isoforms = isoformService.findIsoformsByEntryName(entryName);
		Preconditions.checkArgument(!isoforms.isEmpty(), "No isoforms found for entry " + entryName);

		return isoforms;
	}

	private List<Exon> findExonsAlignedToTranscriptAccordingToEnsembl(String isoformAccession, String refGeneUniqueName, String transcriptAccession, String quality) {

		List<Exon> exons;

		if ("GOLD".equalsIgnoreCase(quality)) {
			exons = geneDAO.findExonsAlignedToTranscriptOfGene(transcriptAccession, refGeneUniqueName);
		} else {
			exons = geneDAO.findExonsPartiallyAlignedToTranscriptOfGene(isoformAccession, transcriptAccession, refGeneUniqueName);
		}

		exons.sort(Comparator.comparingInt(Exon::getFirstPositionOnGene));

		return exons;
	}
}
