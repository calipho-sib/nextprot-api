package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.ChromosomalLocation;
import org.nextprot.api.core.service.annotation.ValidEntry;

import java.util.List;
import java.util.Map;

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
    
    /**
     * A Map representinh the relationships between nextprot entries and ENSG genes
     * The key can be an ENSG identifier and / or a nextprot entry accession number
     * The value is a list of entry accessions or a list of ENSG identifiers (in most cases, the list contain one element)
     * @return a Map
     */
    Map<String,List<String>> getEntryENSGMap();
    
}
