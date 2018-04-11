package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.exon.ExonMapping;
import org.nextprot.api.core.service.annotation.ValidEntry;


public interface EntryExonMappingService {
	
	ExonMapping findExonMappingGeneXIsoformXShorterENST(@ValidEntry String entryName);
}
