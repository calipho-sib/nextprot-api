package org.nextprot.api.commons.dao;

import java.util.List;

public interface MasterIdentifierDao {

	Long findIdByUniqueName(String uniqueName);
	
	List<String> findUniqueNamesOfChromosome(String chromosome);

	List<String> findMasterSequenceUniqueNames();

	List<String> findUniqueNames();
	
	List<String> findUniqueNamesByGeneName(String geneName,String synonym);

}
