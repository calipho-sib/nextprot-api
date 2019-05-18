package org.nextprot.api.core.service;

import java.util.List;

import org.nextprot.api.core.domain.exon.SimpleExonWithSequence;

public interface GeneExonMappingService {
	
	List<SimpleExonWithSequence> findGeneExons(String geneName);
}
