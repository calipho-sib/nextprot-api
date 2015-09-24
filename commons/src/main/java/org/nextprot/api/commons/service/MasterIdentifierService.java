package org.nextprot.api.commons.service;

import java.util.List;
import java.util.Set;

public interface MasterIdentifierService {

	Long findIdByUniqueName(String uniqueName);
	List<String> findUniqueNamesOfChromosome(String chromosome);
	Set<String> findUniqueNames();

}
