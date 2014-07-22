package org.nextprot.api.service;

import java.util.List;

import org.nextprot.api.aop.annotation.ValidEntry;
import org.nextprot.api.domain.mapping.GenomicMapping;

/**
 * Extracts gene / chromosomal information about an entry
 * 
 * @author dteixeira
 */
public interface GenomicMappingService {

	List<GenomicMapping> findGenomicMappingsByEntryName(@ValidEntry String entryName);

}
