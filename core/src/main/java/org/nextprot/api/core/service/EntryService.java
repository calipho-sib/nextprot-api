package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.annotation.ValidEntry;

import java.util.List;

public interface EntryService {

	/**
	 * Retrieve the Entry by name
	 * @param entryName
	 * @return
	 */
	Entry findEntry(@ValidEntry String entryName);

	/**
	 * 
	 * @param entryNames
	 * @return
	 */
	List<Entry> findEntries(List<String> entryNames);
	
	/**
	 * Retrieve the list of entries in a specific chromosome
	 * @param chromosome
	 * @return
	 */
	List<Entry> findEntriesByChromosome(String chromosome);
	
	List<String> findEntryNamesByChromosome(String chromosome);

	default Entry findEntryFromIsoformAccession(String isoformAccession) {

		if (!isoformAccession.contains("-")) {
			return null;
		}

		return findEntry(isoformAccession.split("-")[0]);
	}
}
