package org.nextprot.api.service;

import java.util.List;

import org.nextprot.api.annotation.ValidEntry;
import org.nextprot.api.domain.Entry;

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
	 * Retrieve the list of entries in a specific chromossome
	 * @param chromossome
	 * @return
	 */
	List<Entry> findEntriesByChromossome(String chromossome);
	
	List<String> findEntryNamesByChromossome(String chromossome);
	
}
