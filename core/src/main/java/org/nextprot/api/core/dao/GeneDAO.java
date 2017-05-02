package org.nextprot.api.core.dao;

import org.nextprot.api.core.domain.*;

import java.util.Collection;
import java.util.List;

public interface GeneDAO {

	List<ChromosomalLocation> findChromosomalLocationsByEntryNameOld(String entryName);
	List<ChromosomalLocation> findChromosomalLocationsByEntryName(String entryName);

	List<GenomicMapping> findGenomicMappingByEntryName(String entryName);

	List<TranscriptMapping> findTranscriptsByIsoformNames(Collection<String> isoformNames);

	List<Exon> findExonsAlignedToTranscriptOfGene(String transcriptName, String geneName);

	List<Exon> findExonsPartiallyAlignedToTranscriptOfGene(String isoName, String transcriptName, String geneName);

	List<IsoformMapping> getIsoformMappings(Collection<String> isoformNames);
}
