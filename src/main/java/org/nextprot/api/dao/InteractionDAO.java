package org.nextprot.api.dao;

import java.util.List;

import org.nextprot.api.domain.Interaction;

/**
 * Interaction Data Access Object
 * @author dteixeira
 *
 */
public interface InteractionDAO {

	/**
	 * Find the interactions of an entry, identified by its unique name
	 * @param entryName
	 * @return
	 */
	public List<Interaction> findInteractionsByEntry(String entryName);
	public List<Interaction> findAllInteractions();
	
	
}
