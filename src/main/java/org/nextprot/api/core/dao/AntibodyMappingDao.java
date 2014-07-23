package org.nextprot.api.core.dao;

import java.util.List;

import org.nextprot.api.core.domain.AntibodyMapping;

public interface AntibodyMappingDao {

	/**
	 * Retrieves the mapping of isoforms and antibodies 
	 * @param id
	 * @return
	 */
	List<AntibodyMapping> findAntibodiesById(Long id);
}
