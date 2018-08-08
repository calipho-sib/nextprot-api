package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.ChromosomalLocation;
import org.nextprot.api.core.service.annotation.ValidEntry;

import java.util.List;

/**
 * Extracts gene / chromosomal information about an entry
 * 
 * @author dteixeira
 */
public interface GeneService {

	List<ChromosomalLocation> findChromosomalLocationsByEntry(@ValidEntry String entryName);

    /**
     * @param accession entry to validate gene name
     * @param geneName the gene name
     * @return true if gene name is associated with given entry accession
     */
    boolean isValidGeneName(String accession, String geneName);
}
