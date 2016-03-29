package org.nextprot.api.core.service.impl;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.nextprot.api.core.dao.GeneDAO;
import org.nextprot.api.core.dao.IsoformDAO;
import org.nextprot.api.core.domain.*;
import org.nextprot.api.core.service.GenomicMappingService;
import org.nextprot.api.core.utils.IsoformUtils;
import org.nextprot.api.core.utils.exon.ExonsAnalysisLogger;
import org.nextprot.api.core.utils.exon.TranscriptExonsAnalyser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

@Service
public class GenomicMappingServiceImpl implements GenomicMappingService {

	// -Djava.util.logging.SimpleFormatter.format=%5$s%6$s%n
	private static final Logger LOGGER = Logger.getLogger(GenomicMappingServiceImpl.class.getSimpleName());

	private static final ExonComparator EXON_COMPARATOR = new ExonComparator();
	private static final IsoformMappingComparator ISOFORM_MAPPING_COMPARATOR = new IsoformMappingComparator();

	@Autowired private GeneDAO geneDAO;
	@Autowired private IsoformDAO isoformDAO;

	@Override
	@Cacheable("genomic-mappings")
	public List<GenomicMapping> findGenomicMappingsByEntryName(String entryName) {

		Preconditions.checkArgument((entryName != null) && (entryName.length() > 1), "The entry name "+entryName +" is not valid");

		// Gets all the isoforms for a given entry
		List<Isoform> isoforms = findIsoforms(entryName);

		// Gets all the isoform mappings (done for each gene)
		List<IsoformMapping> isoformMappings = findAndSortIsoformAndTranscriptMappings(isoforms);

		for (IsoformMapping isoformMapping : isoformMappings) {

			for (TranscriptMapping transcriptMapping : isoformMapping.getTranscriptMappings()) { // TODO This will fire multiple queries, but it is the easier way for now

				transcriptMapping.setExons(findAndSortExons(transcriptMapping));
			}

			if (!isoformMapping.getTranscriptMappings().isEmpty()) computeExonListPhasesAndAminoacids(isoformMapping, false);
		}

		// Gets all the genes for a given entry (can be more than one)
		List<GenomicMapping> genomicMappings = findGenomicMappings(entryName, isoformMappings);

		// TODO looks like there is some exons missing, have they changed???? For example this one: ENSE00003030217 is not found for NX_P03372
		//returns a immutable list when the result is cacheable (this prevents modifying the cache, since the cache returns a reference) copy on read and copy on write is too much time consuming
		return new ImmutableList.Builder<GenomicMapping>().addAll(genomicMappings).build();
	}

	private void computeExonListPhasesAndAminoacids(IsoformMapping isoformMapping, boolean logEnabled) {

		List<Entry<Integer, Integer>> positions = isoformMapping.getPositionsOfIsoformOnReferencedGene();

		final int startPositionIsoform = positions.get(0).getKey();
		final int endPositionIsoform = positions.get(positions.size() - 1).getValue();

		TranscriptExonsAnalyser extractor;
		ExonsAnalysisLogger exonInfoLogger = null;

		if (logEnabled) {
			exonInfoLogger = new ExonsAnalysisLogger();
			extractor = new TranscriptExonsAnalyser(exonInfoLogger);
		} else {
			extractor = new TranscriptExonsAnalyser();
		}

		for (TranscriptMapping t : isoformMapping.getTranscriptMappings()) {

			extractor.extract(isoformMapping.getUniqueName() + "." + t.getAccession(), isoformMapping.getBioSequence(), startPositionIsoform, endPositionIsoform, t.getExons());
			if (logEnabled) LOGGER.info(isoformMapping.getUniqueName() + "." + t.getAccession() + "." + t.getReferenceGeneUniqueName() + " (" + t.getQuality() + "): " + exonInfoLogger.getLog());
		}
	}

	private List<Isoform> findIsoforms(String entryName) {

		List<Isoform> isoforms = isoformDAO.findIsoformsByEntryName(entryName);
		Preconditions.checkArgument(!isoforms.isEmpty(), "No isoforms found for entry " + entryName);

		return isoforms;
	}

	private List<IsoformMapping> findAndSortIsoformAndTranscriptMappings(List<Isoform> isoforms) {

		// map isoName -> isoform
		ImmutableMap<String, Isoform> isoformsByName = Maps.uniqueIndex(isoforms, new Function<Isoform, String>() {
			public String apply(Isoform isoform) {
				return isoform.getUniqueName();
			}
		});

		// Find all transcripts mapping all isoforms
		List<TranscriptMapping> transcriptMappings = geneDAO.findTranscriptsByIsoformNames(isoformsByName.keySet());

		// Gets all the isoform mappings (done for each gene)
		List<IsoformMapping> isoformMappings = geneDAO.getIsoformMappings(isoformsByName.keySet());

		// Set isoform and populate transcript mappings
		for (IsoformMapping isoformMapping : isoformMappings) {

			Isoform isoform = isoformsByName.get(isoformMapping.getUniqueName());

			isoformMapping.setIsoform(isoform);

			Collection<TranscriptMapping> tms = Collections2.filter(transcriptMappings, new TranscriptMappingPredicate(isoformMapping));

			isoformMapping.getTranscriptMappings().addAll(tms);
		}

		Collections.sort(isoformMappings, ISOFORM_MAPPING_COMPARATOR);

		return isoformMappings;
	}

	private List<Exon> findAndSortExons(TranscriptMapping transcriptMapping) {

		List<Exon> exons;

		if (transcriptMapping.getQuality().equalsIgnoreCase("GOLD")) {
			exons = geneDAO.findExonsAlignedToTranscriptOfGene(transcriptMapping.getUniqueName(), transcriptMapping.getReferenceGeneUniqueName());
		} else {
			exons = geneDAO.findExonsPartiallyAlignedToTranscriptOfGene(transcriptMapping.getIsoformName(), transcriptMapping.getUniqueName(), transcriptMapping.getReferenceGeneUniqueName());
		}

		Collections.sort(exons, EXON_COMPARATOR);

		return exons;
	}

	private List<GenomicMapping> findGenomicMappings(String entryName, List<IsoformMapping> isoformMappings) {

		List<GenomicMapping> genomicMappings = geneDAO.findGenomicMappingByEntryName(entryName);

		// Puts the isoformMappings to the associated gene and order it
		for (GenomicMapping genomicMapping : genomicMappings) {

			Collection<IsoformMapping> mappings = Collections2.filter(isoformMappings, new IsoformMappingPredicate(genomicMapping.getGeneSeqId()));

			genomicMapping.addAllIsoformMappings(mappings);

			Collections.sort(genomicMapping.getIsoformMappings(), ISOFORM_MAPPING_COMPARATOR);
		}

		return genomicMappings;
	}

	private static class TranscriptMappingPredicate implements Predicate<TranscriptMapping> {

		private final IsoformMapping isoformMapping;

		public TranscriptMappingPredicate(IsoformMapping isoformMapping) {

			this.isoformMapping = isoformMapping;
		}

		@Override
		public boolean apply(TranscriptMapping tm) {

			return tm.getReferenceGeneId() == isoformMapping.getReferenceGeneId() && tm.getIsoformName().equals(isoformMapping.getUniqueName());
		}
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

	private static class ExonComparator implements Comparator<Exon> {

		@Override
		public int compare(Exon e1, Exon e2) {
			return e1.getFirstPositionOnGene() - e2.getFirstPositionOnGene();
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
