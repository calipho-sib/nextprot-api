package org.nextprot.api.core.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.nextprot.api.core.dao.GeneDAO;
import org.nextprot.api.core.domain.*;
import org.nextprot.api.core.service.GenomicMappingService;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.utils.IsoformUtils;
import org.nextprot.api.core.utils.exon.ExonsAnalysisLogger;
import org.nextprot.api.core.utils.exon.TranscriptExonsAnalyser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class GenomicMappingServiceImpl implements GenomicMappingService {

	// -Djava.util.logging.SimpleFormatter.format=%5$s%6$s%n
	private static final Logger LOGGER = Logger.getLogger(GenomicMappingServiceImpl.class.getSimpleName());

	@Autowired private GeneDAO geneDAO;
	@Autowired private IsoformService isoformService;

	@Override
	@Cacheable("genomic-mappings")
	public List<GenomicMapping> findGenomicMappingsByEntryName(String entryName) {

		Objects.requireNonNull(entryName, "The entry name "+entryName +" is not defined");
		Preconditions.checkArgument(!entryName.isEmpty(), "The entry name "+entryName +" is not empty");

		// Gets all the isoform mappings (done for each gene)
		List<IsoformMapping> isoformMappings = findIsoformMappingList(entryName);

		//
		for (IsoformMapping isoformMapping : isoformMappings) {

			for (TranscriptMapping transcriptMapping : isoformMapping.getTranscriptMappings()) {
				transcriptMapping.setExons(findAndSortExons(transcriptMapping));
			}

			if (!isoformMapping.getTranscriptMappings().isEmpty()) {
				computeExonListPhasesAndAminoacids(isoformMapping);
			}
		}

		List<GenomicMapping> genomicMappings = geneDAO.findGenomicMappingByEntryName(entryName);

        setIsoformMappingsInGenomicMapping(genomicMappings, isoformMappings);

		// TODO looks like there is some exons missing, have they changed???? For example this one: ENSE00003030217 is not found for NX_P03372
		//returns a immutable list when the result is cacheable (this prevents modifying the cache, since the cache returns a reference) copy on read and copy on write is too much time consuming
		return new ImmutableList.Builder<GenomicMapping>().addAll(genomicMappings).build();
	}

	private List<IsoformMapping> findIsoformMappingList(String entryName) {

		Map<String, Isoform> isoformsByName = findIsoforms(entryName).stream()
				.collect(Collectors.toMap(Isoform::getIsoformAccession, Function.identity()));

		// Find all transcripts mapping all isoforms
		List<TranscriptMapping> transcriptMappings = geneDAO.findTranscriptsByIsoformNames(isoformsByName.keySet());

		// Gets all the isoform mappings (done for each gene)
		List<IsoformMapping> isoformMappings = geneDAO.getIsoformMappings(isoformsByName.keySet());

		// Set missing fields of IsoformMappings
		for (IsoformMapping isoformMapping : isoformMappings) {

			isoformMapping.setIsoform(isoformsByName.get(isoformMapping.getUniqueName()));

			List<TranscriptMapping> tms = transcriptMappings.stream()
					.filter(tm -> tm.getReferenceGeneId() == isoformMapping.getReferenceGeneId() &&
							tm.getIsoformName().equals(isoformMapping.getUniqueName()))
					.collect(Collectors.toList());

			isoformMapping.setTranscriptMappings(tms);
		}

		isoformMappings.sort((im1, im2) -> new IsoformUtils.IsoformComparator().compare(im1.getIsoform(), im2.getIsoform()));

		return isoformMappings;
	}

	private void computeExonListPhasesAndAminoacids(IsoformMapping isoformMapping) {

		List<Entry<Integer, Integer>> positions = isoformMapping.getPositionsOfIsoformOnReferencedGene();

		final int startPositionIsoform = positions.get(0).getKey();
		final int endPositionIsoform = positions.get(positions.size() - 1).getValue();

		TranscriptExonsAnalyser extractor;
		ExonsAnalysisLogger exonInfoLogger = new ExonsAnalysisLogger();
		extractor = new TranscriptExonsAnalyser(exonInfoLogger);

		for (TranscriptMapping t : isoformMapping.getTranscriptMappings()) {

			extractor.extract(isoformMapping.getUniqueName() + "." + t.getAccession(), isoformMapping.getBioSequence(), startPositionIsoform, endPositionIsoform, t.getExons());
			LOGGER.info(isoformMapping.getUniqueName() + "." + t.getAccession() + "." + t.getReferenceGeneUniqueName() + " (" + t.getQuality() + "): " + exonInfoLogger.getLog());
		}
	}

	private List<Isoform> findIsoforms(String entryName) {

		List<Isoform> isoforms = isoformService.findIsoformsByEntryName(entryName);
		Preconditions.checkArgument(!isoforms.isEmpty(), "No isoforms found for entry " + entryName);

		return isoforms;
	}

	private List<Exon> findAndSortExons(TranscriptMapping transcriptMapping) {

		List<Exon> exons;

		String refGeneUniqueName = transcriptMapping.getReferenceGeneUniqueName();

		if ("GOLD".equalsIgnoreCase(transcriptMapping.getQuality())) {
			exons = geneDAO.findExonsAlignedToTranscriptOfGene(transcriptMapping.getUniqueName(), refGeneUniqueName);
		} else {
			exons = geneDAO.findExonsPartiallyAlignedToTranscriptOfGene(transcriptMapping.getIsoformName(), transcriptMapping.getUniqueName(), refGeneUniqueName);
		}

		exons.sort(Comparator.comparingInt(Exon::getFirstPositionOnGene));

		return exons;
	}

	private void setIsoformMappingsInGenomicMapping(List<GenomicMapping> genomicMappings, List<IsoformMapping> isoformMappings) {

		Comparator<Isoform> isoformComparator = new IsoformUtils.IsoformComparator();

		// Puts the isoformMappings to the associated gene and order it
		for (GenomicMapping genomicMapping : genomicMappings) {

			List<IsoformMapping> mappings = isoformMappings.stream()
					.filter(im -> im.getReferenceGeneId() == genomicMapping.getGeneSeqId())
					.collect(Collectors.toList());

			genomicMapping.addAllIsoformMappings(mappings);
			genomicMapping.getIsoformMappings().sort((im1, im2) -> isoformComparator.compare(im1.getIsoform(), im2.getIsoform()));
		}
	}
}
