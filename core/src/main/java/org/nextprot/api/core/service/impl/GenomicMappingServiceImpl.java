package org.nextprot.api.core.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
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
		Collection<IsoformMapping> isoformMappings = findIsoformMappings(entryName);

		isoformMappings.forEach(isoformMapping -> analyseExons(isoformMapping));

		// Puts the isoformMappings to the associated gene and order it
		for (GenomicMapping genomicMapping : genomicMappings) {

			List<IsoformMapping> mappings = isoformMappings.stream()
					.filter(im -> im.getReferenceGeneId() == genomicMapping.getGeneSeqId())
					.collect(Collectors.toList());

			genomicMapping.addAllIsoformMappings(mappings);
			genomicMapping.getIsoformMappings().sort((im1, im2) -> isoformComparator.compare(im1.getIsoform(), im2.getIsoform()));
		}

		return new ImmutableList.Builder<GenomicMapping>().addAll(genomicMappings).build();
	}

	private Collection<IsoformMapping> findIsoformMappings(String entryName) {

		Map<String, Isoform> isoformsByName = findIsoforms(entryName).stream()
				.collect(Collectors.toMap(Isoform::getIsoformAccession, Function.identity()));

        Map<String, List<IsoformMapping>> isoformMappingsByIsoformName = geneDAO.getIsoformMappingsByIsoformName(isoformsByName.keySet());
        Map<String, List<TranscriptMapping>> transcriptMappingsByIsoformName = geneDAO.findTranscriptMappingsByIsoformName(isoformsByName.keySet());

		// Set missing fields of IsoformMappings
		for (String isoformName : isoformMappingsByIsoformName.keySet()) {

		    for (IsoformMapping isoformMapping : isoformMappingsByIsoformName.get(isoformName)) {

				isoformMapping.setIsoform(isoformsByName.get(isoformName));

				// filter transcript mappings by gene name
				List<TranscriptMapping> transcriptMappings = transcriptMappingsByIsoformName.get(isoformName).stream()
						.filter(tm -> tm.getReferenceGeneId() == isoformMapping.getReferenceGeneId())
						.collect(Collectors.toList());

				for (TranscriptMapping transcriptMapping : transcriptMappings) {
					findAndSetExonsAlignedToTranscript(transcriptMapping);
				}

				isoformMapping.setTranscriptMappings(transcriptMappings);
			}
		}

		return isoformMappingsByIsoformName.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
	}

	private void analyseExons(IsoformMapping isoformMapping) {

		isoformMapping.getTranscriptMappings().forEach(transcriptMapping ->
			computeExonListPhasesAndAminoacids(transcriptMapping, isoformMapping.getBioSequence(),
						isoformMapping.getFirstPositionIsoformOnGene(),
						isoformMapping.getLastPositionIsoformOnGene()));
	}

	private void computeExonListPhasesAndAminoacids(TranscriptMapping transcriptMapping, String bioSequence, int startPositionIsoformOnGene, int endPositionIsoformOnGene) {

		TranscriptExonsAnalyser analyser;
		ExonsAnalysisMessageBuilder exonsAnalysisMessageBuilder = new ExonsAnalysisMessageBuilder();
        analyser = new TranscriptExonsAnalyser(exonsAnalysisMessageBuilder);

        boolean success = analyser.analyse(bioSequence, startPositionIsoformOnGene, endPositionIsoformOnGene,
				transcriptMapping.getExons());

        if (success) {
			LOGGER.info(transcriptMapping.getIsoformName() + "." + transcriptMapping.getAccession() + "." + transcriptMapping.getReferenceGeneUniqueName() + " (" + transcriptMapping.getQuality() + "): " + exonsAnalysisMessageBuilder.getMessage());
		}
		else {
			LOGGER.severe("MAPPING ERROR: " + transcriptMapping.getIsoformName() + "." + transcriptMapping.getAccession() + "." + transcriptMapping.getReferenceGeneUniqueName() + " (" + transcriptMapping.getQuality() + "): " + exonsAnalysisMessageBuilder.getMessage());
		}
	}

	private List<Isoform> findIsoforms(String entryName) {

		List<Isoform> isoforms = isoformService.findIsoformsByEntryName(entryName);
		Preconditions.checkArgument(!isoforms.isEmpty(), "No isoforms found for entry " + entryName);

		return isoforms;
	}

	private void findAndSetExonsAlignedToTranscript(TranscriptMapping transcriptMapping) {

		List<Exon> exons;

		String refGeneUniqueName = transcriptMapping.getReferenceGeneUniqueName();

		if ("GOLD".equalsIgnoreCase(transcriptMapping.getQuality())) {
			exons = geneDAO.findExonsAlignedToTranscriptOfGene(transcriptMapping.getUniqueName(), refGeneUniqueName);
		} else {
			exons = geneDAO.findExonsPartiallyAlignedToTranscriptOfGene(transcriptMapping.getIsoformName(), transcriptMapping.getUniqueName(), refGeneUniqueName);
		}

		exons.sort(Comparator.comparingInt(Exon::getFirstPositionOnGene));

		transcriptMapping.setExons(exons);
	}
}
