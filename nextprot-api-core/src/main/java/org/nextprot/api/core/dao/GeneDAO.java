package org.nextprot.api.core.dao;

import java.util.List;

import org.nextprot.api.core.domain.ChromosomalLocation;
import org.nextprot.api.core.domain.Exon;
import org.nextprot.api.core.domain.GenomicMapping;
import org.nextprot.api.core.domain.IsoformMapping;
import org.nextprot.api.core.domain.TranscriptMapping;

public interface GeneDAO {

	List<ChromosomalLocation> findChromosomalLocationsByEntryName(String entryName);

	List<GenomicMapping> findGenomicMappingByEntryName(String entryName);

	List<TranscriptMapping> findTranscriptsByIsoformNames(List<String> isoformNames);

	List<Exon> findExonsAlignedToTranscriptOfGene(String transcriptName, String geneName);

	List<Exon> findExonsPartiallyAlignedToTranscriptOfGene(String isoName, String transcriptName, String geneName);

	List<IsoformMapping> getIsoformMappings(List<String> isoformNames);
}
