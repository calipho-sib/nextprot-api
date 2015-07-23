package org.nextprot.api.core.service.impl;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.*;
import org.nextprot.api.core.dao.GeneDAO;
import org.nextprot.api.core.dao.IsoformDAO;
import org.nextprot.api.core.domain.*;
import org.nextprot.api.core.service.GenomicMappingService;
import org.nextprot.api.core.utils.exon.TranscriptInfosExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;

@Service
public class GenomicMappingServiceImpl implements GenomicMappingService {

	private static final Logger LOGGER = Logger.getLogger(GenomicMappingServiceImpl.class.getSimpleName());

	@Autowired private GeneDAO geneDAO;
	@Autowired private IsoformDAO isoformDAO;

	@Override
	@Cacheable("genomic-mappings")
	public List<GenomicMapping> findGenomicMappingsByEntryName(String entryName) {

		Preconditions.checkArgument((entryName != null) && (entryName.length() > 1), "The entry name is not valid");
		// ////////////////////////////////////// GENES //////////////////////////////////////////////////////////////////////////////////

		// Gets all the genes for a given entry (can be more than one)
		List<GenomicMapping> genomicMappings = geneDAO.findGenomicMappingByEntryName(entryName);
		Preconditions.checkArgument(!genomicMappings.isEmpty(), "No genomic mapping found for entry" + entryName);

		// ////////////////////////////////////// ISOFORMS //////////////////////////////////////////////////////////////////////////////////

		// Gets all the isoforms
		List<Isoform> isoforms = isoformDAO.findIsoformsByEntryName(entryName);
		List<String> isoformNames = Lists.transform(isoforms, new IsoformNameFunction());
		ImmutableMap<String, Isoform> isoformsByName = Maps.uniqueIndex(isoforms, new Function<Isoform, String>() {
			public String apply(Isoform isoform) {
				return isoform.getUniqueName();
			}
		});

		Preconditions.checkArgument(!isoformNames.isEmpty(), "No isoforms found for entry " + entryName);

		// Gets all the isoform mappings (done for each gene)
		List<IsoformMapping> isoformMappings = geneDAO.getIsoformMappings(isoformNames);

		for (IsoformMapping isoformMapping : isoformMappings) {
			Isoform isoform = isoformsByName.get(isoformMapping.getUniqueName());
			isoformMapping.setIsoform(isoform);
		}

		Multimap<Long, IsoformMapping> isoformsMappingsByGeneId = Multimaps.index(isoformMappings, new Function<IsoformMapping, Long>() {
			public Long apply(IsoformMapping isoformMapping) {
				return isoformMapping.getReferenceGeneId();
			}
		});

		// Puts the isoformMappings to the associated gene and order it
		for (GenomicMapping genomicMapping : genomicMappings) {
			Collection<IsoformMapping> mappings = isoformsMappingsByGeneId.get(genomicMapping.getGeneSeqId());

			List<IsoformMapping> ims = new ArrayList<IsoformMapping>(mappings);
			// Sorts the list alphabetically
			Collections.sort(ims, new Comparator<IsoformMapping>() {
				@Override
				public int compare(IsoformMapping im1, IsoformMapping im2) {
					return im1.getUniqueName().compareTo(im2.getUniqueName());
				}
			});

			genomicMapping.getIsoformMappings().addAll(ims);
		}

		// ////////////////////////////////////// TRANSCRIPTS //////////////////////////////////////////////////////////////////////////////////

		// Should get different transcripts for each isoforms
		List<TranscriptMapping> transcriptMappings = geneDAO.findTranscriptsByIsoformNames(isoformNames);
		Multimap<String, TranscriptMapping> transcriptMappingsByGeneIdAndIsoName = Multimaps.index(transcriptMappings, new Function<TranscriptMapping, String>() {
			public String apply(TranscriptMapping transcriptMapping) {
				return transcriptMapping.getReferenceGeneId() + transcriptMapping.getIsoformName();
			}
		});

		for (IsoformMapping isoformMapping : isoformMappings) {
			String key = isoformMapping.getReferenceGeneId() + isoformMapping.getUniqueName();
			Collection<TranscriptMapping> tms = transcriptMappingsByGeneIdAndIsoName.get(key);
			isoformMapping.getTranscriptMappings().addAll(tms);
		}

		// ////////////////////////////////////// EXONS //////////////////////////////////////////////////////////////////////////////////

		for (TranscriptMapping t : transcriptMappings) { // TODO This will fire multiple queries, but it is the easier way for now

			Collection<Exon> exons = null;

			if (t.getQuality().equalsIgnoreCase("GOLD")) {
				exons = geneDAO.findExonsAlignedToTranscriptOfGene(t.getUniqueName(), t.getReferenceGeneUniqueName());
			} else {
				exons = geneDAO.findExonsPartiallyAlignedToTranscriptOfGene(t.getIsoformName(), t.getUniqueName(), t.getReferenceGeneUniqueName());
			}

			List<Exon> sortedExons = new ArrayList<Exon>(exons);
			Collections.sort(sortedExons, new Comparator<Exon>() {
				@Override
				public int compare(Exon e1, Exon e2) {
					return e1.getFirstPositionOnGene() - e2.getFirstPositionOnGene();
				}
			});
//			System.err.println("Setting " + sortedExons.size() + " for " + t.getUniqueName() + " and isoform " + t.getIsoformName());
			t.setExons(sortedExons);
		}

		Collections.sort(isoformMappings, new Comparator<IsoformMapping>() {
			@Override
			public int compare(IsoformMapping im1, IsoformMapping im2) {
				return im1.getUniqueName().compareTo(im2.getUniqueName());
			}
		});

		// Computes exon compositions for each isoform mapping
		for (IsoformMapping isoformMapping : isoformMappings) {
			computeExonCompositions(isoformMapping);
		}

		// TODO looks like there is some exons missing, have they changed???? For example this one: ENSE00003030217 is not found for NX_P03372
		//returns a immutable list when the result is cacheable (this prevents modifying the cache, since the cache returns a reference) copy on read and copy on write is too much time consuming
		return new ImmutableList.Builder<GenomicMapping>().addAll(genomicMappings).build();

	}

	private void computeExonCompositions(IsoformMapping isoformMapping) {

		List<Entry<Integer, Integer>> positions = isoformMapping.getPositionsOfIsoformOnReferencedGene();

		final int startPositionIsoform = positions.get(0).getKey();
		final int endPositionIsoform = positions.get(positions.size() - 1).getValue();

		//TranscriptInfoLogger exonInfoLogger = new TranscriptInfoLogger();
		//TranscriptInfosExtractor extractor = new TranscriptInfosExtractor(exonInfoLogger);
		TranscriptInfosExtractor extractor = new TranscriptInfosExtractor();

		for (TranscriptMapping t : isoformMapping.getTranscriptMappings()) {

			extractor.extract(isoformMapping.getUniqueName() + "." + t.getAccession(), isoformMapping.getBioSequence(), startPositionIsoform, endPositionIsoform, t.getExons());
			//LOGGER.info(isoformMapping.getUniqueName() + "." + t.getAccession() + ": " + exonInfoLogger.getInfos());
		}
	}

	private class IsoformNameFunction implements Function<Isoform, String> {
		public String apply(Isoform isoform) {
			return isoform.getUniqueName();
		}
	}
}
