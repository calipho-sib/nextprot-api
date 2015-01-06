package org.nextprot.api.commons.dao;

import java.util.List;

public interface MasterIdentifierDao {

	Long findIdByUniqueName(String uniqueName);
	
	List<String> findUniqueNamesOfChromossome(String chromossome);

	List<String> findMasterSequenceUniqueNames();

	List<String> findUniqueNames();
		
}
