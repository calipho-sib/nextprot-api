package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.GenomicMapping;
import org.nextprot.api.core.service.annotation.ValidEntry;

import java.util.Map;

/**
 * Extracts gene / chromosomal information about an entry
 * 
 * @author dteixeira
 * @author fnikitin
 */
public interface GenomicMappingService {

	/**
	 * Find genomic mappings for an entry accession
	 * @param entryName the neXtProt entry accession
	 * @return the genomic mappings by gene accession
	 */
	Map<String, GenomicMapping> findGenomicMappingsByEntryName(@ValidEntry String entryName);
}
