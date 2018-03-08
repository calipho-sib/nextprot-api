package org.nextprot.api.core.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
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

import java.util.*;
import java.util.Map.Entry;
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

		for (IsoformMapping isoformMapping : isoformMappings) {

			for (TranscriptMapping transcriptMapping : isoformMapping.getTranscriptMappings()) { // TODO This will fire multiple queries, but it is the easier way for now

				transcriptMapping.setExons(findAndSortExons(transcriptMapping));
			}

			if (!isoformMapping.getTranscriptMappings().isEmpty()) computeExonListPhasesAndAminoacids(isoformMapping);
		}

		// Gets all the genes for a given entry (can be more than one)
		List<GenomicMapping> genomicMappings = findGenomicMappings(entryName, isoformMappings);

		// TODO looks like there is some exons missing, have they changed???? For example this one: ENSE00003030217 is not found for NX_P03372
		//returns a immutable list when the result is cacheable (this prevents modifying the cache, since the cache returns a reference) copy on read and copy on write is too much time consuming
		return new ImmutableList.Builder<GenomicMapping>().addAll(genomicMappings).build();
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

		isoformMappings.sort(new IsoformMappingComparator());

		return isoformMappings;
	}

	private List<Exon> findAndSortExons(TranscriptMapping transcriptMapping) {

		List<Exon> exons;

		if ("GOLD".equalsIgnoreCase(transcriptMapping.getQuality())) {
			exons = geneDAO.findExonsAlignedToTranscriptOfGene(transcriptMapping.getUniqueName(), transcriptMapping.getReferenceGeneUniqueName());
		} else {
			exons = geneDAO.findExonsPartiallyAlignedToTranscriptOfGene(transcriptMapping.getIsoformName(), transcriptMapping.getUniqueName(), transcriptMapping.getReferenceGeneUniqueName());
		}

		exons.sort(Comparator.comparingInt(Exon::getFirstPositionOnGene));

		return exons;
	}

	private List<GenomicMapping> findGenomicMappings(String entryName, List<IsoformMapping> isoformMappings) {

		List<GenomicMapping> genomicMappings = geneDAO.findGenomicMappingByEntryName(entryName);

		// Puts the isoformMappings to the associated gene and order it
		for (GenomicMapping genomicMapping : genomicMappings) {

			Collection<IsoformMapping> mappings = Collections2.filter(isoformMappings, new IsoformMappingPredicate(genomicMapping.getGeneSeqId()));

			genomicMapping.addAllIsoformMappings(mappings);

			genomicMapping.getIsoformMappings().sort(new IsoformMappingComparator());
		}

		return genomicMappings;
	}

	private static class IsoformMappingPredicate implements Predicate<IsoformMapping> {

		private final long referenceGeneId;

		public IsoformMappingPredicate(long referenceGeneId) {

			this.referenceGeneId = referenceGeneId;
		}

		@Override
		public boolean apply(IsoformMapping im) {

			return im.getReferenceGeneId() == referenceGeneId;
		}
	}

    /**
     * Comparison done by isoforms defined in IsoformUtils.IsoformComparator
     */
    static class IsoformMappingComparator implements Comparator<IsoformMapping> {

        final private IsoformUtils.IsoformComparator comparator = new IsoformUtils.IsoformComparator();

		@Override
		public int compare(IsoformMapping im1, IsoformMapping im2) {

			return comparator.compare(im1.getIsoform(), im2.getIsoform());
		}
	}
}
