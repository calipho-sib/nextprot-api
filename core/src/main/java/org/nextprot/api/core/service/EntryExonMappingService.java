package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.exon.ExonMapping;
import org.nextprot.api.core.service.annotation.ValidEntry;

import java.util.Set;


public interface EntryExonMappingService {
	
	ExonMapping findExonMappingGeneXIsoformXShorterENST(@ValidEntry String entryName, String geneName);
	Set<String> findENSGs(@ValidEntry String entryName);
}
