package org.nextprot.api.core.service;

import java.util.List;

import org.nextprot.api.core.domain.GeneRegion;
import org.nextprot.api.core.domain.exon.SimpleExonWithSequence;

public interface GeneMasterIsoformMappingService {
	
	List<SimpleExonWithSequence> findGeneExons(String geneName);
	List<GeneRegion> findEntryGeneRegions(String entryName);
}
