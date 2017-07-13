package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.annotation.ValidEntry;

import java.util.List;

/**
 * Extracts information related to the isoforms
 * 
 * @author dteixeira
 */
public interface IsoformService {

	List<Isoform> findIsoformsByEntryName(@ValidEntry String entryName);

	/**
	 * Find the isoform of the given entry matching the given name
	 * @param entryAccession the entry accession
	 * @param name the isoform name (any of its name among accession, synonyms...)
	 * @return the found isoform or null if not found
	 */
	Isoform findIsoformByName(String entryAccession, String name);

	/**
	 * Find the isoform of the given entry matching the given accession
	 * @param isoformAccession the isoform accession
	 * @return the found isoform or null if not found
	 */
	default Isoform findIsoformByAccession(String isoformAccession) {

		if (!isoformAccession.contains("-")) {
			return null;
		}

		return findIsoformByName(isoformAccession.split("-")[0], isoformAccession);
	}
}
