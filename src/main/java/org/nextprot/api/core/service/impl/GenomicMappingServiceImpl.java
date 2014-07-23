package org.nextprot.api.core.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.utils.MappingUtils;
import org.nextprot.api.core.dao.GeneDAO;
import org.nextprot.api.core.dao.IsoformDAO;
import org.nextprot.api.core.domain.AminoAcid;
import org.nextprot.api.core.domain.Exon;
import org.nextprot.api.core.domain.GenomicMapping;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.IsoformMapping;
import org.nextprot.api.core.domain.TranscriptMapping;
import org.nextprot.api.core.service.GenomicMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

@Lazy
@Service
public class GenomicMappingServiceImpl implements GenomicMappingService {

	// private static final Log LOGGER = LogFactory.getLog(GenomicMappingServiceImpl.class);
	private final static Log LOGGER = LogFactory.getLog(GenomicMappingServiceImpl.class);

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
		return genomicMappings;

	}

	private void computeExonCompositions(IsoformMapping isoformMapping) {

		List<Entry<Integer, Integer>> positions = isoformMapping.getPositionsOfIsoformOnReferencedGene();
		final int startPositionIsoform = positions.get(0).getKey();
		final int endPositionIsoform = positions.get(positions.size() - 1).getValue();
		final String isoformSequence = isoformMapping.getBioSequence();

		for (TranscriptMapping t : isoformMapping.getTranscriptMappings()) {

			// System.err.println(t.getAccession());
			// In case of http://www.nextprot.org/db/entry/NX_O15519/exons how is it done, when there is no transcript???

			int currentPosition = 0;

			for (Exon e : t.getExons()) {

				final int startPositionExon = e.getFirstPositionOnGene();
				final int endPositionExon = e.getLastPositionOnGene();

				int startPositionCoding = startPositionExon;
				int endPositionCoding = endPositionExon;

				String codingStatus = MappingUtils.getExonCodingStatus(startPositionIsoform, endPositionIsoform, startPositionExon, endPositionExon);
				e.setCodingStatus(codingStatus);

				if (codingStatus.equals("STOP_ONLY") || codingStatus.equals("NOT_CODING")) {
					continue; // in this case there is no amino acid
				} else if (codingStatus.equals("START")) {
					startPositionCoding = startPositionIsoform;
				} else if (codingStatus.equals("STOP")) {
					endPositionCoding = endPositionIsoform;
				} else if (codingStatus.equals("MONO")) {
					startPositionCoding = startPositionIsoform;
					endPositionCoding = endPositionIsoform;
				}

				int firstPosition = (int) currentPosition / 3;
				int firstPhase = (int) currentPosition % 3;

				int exonCodingLength = endPositionCoding - startPositionCoding + 1;
				currentPosition += exonCodingLength;

				int lastPosition = (int) currentPosition / 3;
				int lastPhase = (int) currentPosition % 3;

				if (lastPosition > (isoformSequence.length() - 1))
					lastPosition = isoformSequence.length() - 1;

//				System.err.println(e.getAccession() + " startPositionExon:" + startPositionExon + " endPositionExon:" + endPositionExon + " exonLength:" + (endPositionExon - startPositionExon)
//						+ " firstPosition: " + firstPosition + " lastPosition: " + lastPosition + " coding:" + codingStatus + " startOnIsoform:" + startPositionIsoform + " endOnIsoform"
//						+ endPositionIsoform + " iso name:" + isoformMapping.getIsoMainName() + " seq size:" + isoformSequence.length() + " transcript name:" + t.getAccession());

				e.setFirstAminoAcid(getFirstAminoAcid(isoformSequence, firstPosition, firstPhase));
				e.setLastAminoAcid(getLastAminoAcid(isoformSequence, lastPosition, lastPhase));

			}
		}

	}

	private static AminoAcid getLastAminoAcid(String isoformSequence, int position, int phase) {

		if ((position > 0) && (phase == 0) && (position + 1 != isoformSequence.length())) { // It should not be the end ot the isoform
			position = position - 1;
		}

		if (position > isoformSequence.length()) {
			position = isoformSequence.length() - 1;
			System.err.println("Why is this happeningm for the last??? Isoform length" + isoformSequence.length() + " position=" + position + " phase=" + phase);
		}

		return new AminoAcid(position + 1, phase, isoformSequence.charAt(position));
	}

	private static AminoAcid getFirstAminoAcid(String isoformSequence, int position, int phase) {

		// Check NX_A6NC05
		if (position == isoformSequence.length()) {
			position = isoformSequence.length() - 1;
		}

		if (position > isoformSequence.length()) {
			position = isoformSequence.length() - 1;
			LOGGER.warn("Why the is this happening for the first amino acid??? Isoform length" + isoformSequence.length() + " position=" + position + " phase=" + phase);
		}

		return new AminoAcid(position + 1, phase, isoformSequence.charAt(position));
	}

	private class IsoformNameFunction implements Function<Isoform, String> {
		public String apply(Isoform isoform) {
			return isoform.getUniqueName();
		}
	}

}
