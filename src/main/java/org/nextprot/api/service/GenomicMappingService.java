package org.nextprot.api.service;

import java.util.List;

import org.nextprot.api.domain.GenomicMapping;
import org.nextprot.api.service.annotation.ValidEntry;

/**
 * Extracts gene / chromosomal information about an entry
 * 
 * @author dteixeira
 */
public interface GenomicMappingService {

	List<GenomicMapping> findGenomicMappingsByEntryName(@ValidEntry String entryName);

}
