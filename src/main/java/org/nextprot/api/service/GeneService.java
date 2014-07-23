package org.nextprot.api.service;

import java.util.List;

import org.nextprot.api.domain.ChromosomalLocation;
import org.nextprot.api.service.annotation.ValidEntry;

/**
 * Extracts gene / chromosomal information about an entry
 * 
 * @author dteixeira
 */
public interface GeneService {

	List<ChromosomalLocation> findChromosomalLocationsByEntry(@ValidEntry String entryName);

}
