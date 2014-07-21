package org.nextprot.api.service;

import org.nextprot.api.service.impl.FluentEntryServiceImpl.FluentEntry;

/**
 * Fluent interface to create a new entry
 * @author dteixeira
 *
 */
public interface FluentEntryService {
	
	public FluentEntry getNewEntry(String entryName);

}
