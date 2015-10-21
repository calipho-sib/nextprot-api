package org.nextprot.api.core.dao;

import java.util.List;

import org.nextprot.api.core.domain.AntibodyMapping;
import org.nextprot.api.core.domain.annotation.Annotation;

public interface AntibodyMappingDao {

	/**
	 * Retrieves the mapping of isoforms and antibodies 
	 * @param id
	 * @return
	 */
	List<AntibodyMapping> findAntibodiesById(Long id);

	List<Annotation> findAntibodyMappingAnnotationsById(Long masterId);
}
