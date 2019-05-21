package org.nextprot.api.core.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.nextprot.api.core.domain.ChromosomalLocation;
import org.nextprot.api.core.domain.GeneRegion;
import org.nextprot.api.core.domain.GenomicMapping;
import org.nextprot.api.core.domain.IsoformGeneMapping;
import org.nextprot.api.core.domain.TranscriptGeneMapping;
import org.nextprot.api.core.domain.exon.SimpleExon;
import org.nextprot.api.core.domain.exon.SimpleExonWithSequence;

public interface GeneDAO {

	List<ChromosomalLocation> findChromosomalLocationsByEntryNameOld(String entryName);
	List<ChromosomalLocation> findChromosomalLocationsByEntryName(String entryName);

	List<GenomicMapping> findGenomicMappingByEntryName(String entryName);

	Map<String, List<TranscriptGeneMapping>> findTranscriptMappingsByIsoformName(Collection<String> isoformNames);

	List<SimpleExon> findExonsAlignedToTranscriptOfGene(String transcriptName, String geneName);

	List<SimpleExon> findExonsPartiallyAlignedToTranscriptOfGene(String isoName, String transcriptName, String geneName);

	Map<String, List<IsoformGeneMapping>> getIsoformMappingsByIsoformName(Collection<String> isoformNames);

	List<SimpleExonWithSequence> findExonsOfGene(String geneName);
	List<GeneRegion> findMasterGeneRegions(String entryName);

}
