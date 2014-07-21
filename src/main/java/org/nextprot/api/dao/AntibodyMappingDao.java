package org.nextprot.api.dao;

import java.util.List;

import org.nextprot.api.domain.AntibodyMapping;

public interface AntibodyMappingDao {

	/**
	 * Retrieves the mapping of isoforms and antibodies 
	 * @param id
	 * @return
	 */
	List<AntibodyMapping> findAntibodiesById(Long id);
}
