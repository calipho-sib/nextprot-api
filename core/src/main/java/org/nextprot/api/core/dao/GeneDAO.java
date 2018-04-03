package org.nextprot.api.core.dao;

import org.nextprot.api.core.domain.ChromosomalLocation;
import org.nextprot.api.core.domain.GenomicMapping;
import org.nextprot.api.core.domain.IsoformGeneMapping;
import org.nextprot.api.core.domain.TranscriptGeneMapping;
import org.nextprot.api.core.domain.exon.UncategorizedExon;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface GeneDAO {

	List<ChromosomalLocation> findChromosomalLocationsByEntryNameOld(String entryName);
	List<ChromosomalLocation> findChromosomalLocationsByEntryName(String entryName);

	List<GenomicMapping> findGenomicMappingByEntryName(String entryName);

	Map<String, List<TranscriptGeneMapping>> findTranscriptMappingsByIsoformName(Collection<String> isoformNames);

	List<UncategorizedExon> findExonsAlignedToTranscriptOfGene(String transcriptName, String geneName);

	List<UncategorizedExon> findExonsPartiallyAlignedToTranscriptOfGene(String isoName, String transcriptName, String geneName);

	Map<String, List<IsoformGeneMapping>> getIsoformMappingsByIsoformName(Collection<String> isoformNames);
}
