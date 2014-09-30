package org.nextprot.api.core.service;

import java.util.List;

import org.nextprot.api.core.domain.GenomicMapping;
import org.nextprot.api.core.service.annotation.ValidEntry;

/**
 * Extracts gene / chromosomal information about an entry
 * 
 * @author dteixeira
 */
public interface GenomicMappingService {

	List<GenomicMapping> findGenomicMappingsByEntryName(@ValidEntry String entryName);

}
