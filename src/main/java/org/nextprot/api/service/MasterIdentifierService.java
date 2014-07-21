package org.nextprot.api.service;

import java.util.List;

public interface MasterIdentifierService {

	Long findIdByUniqueName(String uniqueName);
	List<String> findUniqueNamesOfChromossome(String chromossome);
	List<String> findUniqueNames();

}
