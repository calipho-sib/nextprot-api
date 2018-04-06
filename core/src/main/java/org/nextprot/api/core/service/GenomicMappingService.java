package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.GenomicMapping;
import org.nextprot.api.core.service.annotation.ValidEntry;

import java.util.Map;

/**
 * Extracts gene / chromosomal information about an entry
 * 
 * @author dteixeira
 */
public interface GenomicMappingService {

	Map<String, GenomicMapping> findGenomicMappingsByEntryName(@ValidEntry String entryName);
}
