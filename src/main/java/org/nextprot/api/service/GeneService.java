package org.nextprot.api.service;

import java.util.List;

import org.nextprot.api.annotation.ValidEntry;
import org.nextprot.api.domain.ChromosomalLocation;

/**
 * Extracts gene / chromosomal information about an entry
 * 
 * @author dteixeira
 */
public interface GeneService {

	List<ChromosomalLocation> findChromosomalLocationsByEntry(@ValidEntry String entryName);

}
