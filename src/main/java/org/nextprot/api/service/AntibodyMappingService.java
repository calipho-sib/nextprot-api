package org.nextprot.api.service;

import java.util.List;

import org.nextprot.api.annotation.ValidEntry;
import org.nextprot.api.domain.AntibodyMapping;

public interface AntibodyMappingService {

	/**
	 * 
	 * @param id
	 * @return
	 */
	List<AntibodyMapping> findAntibodyMappingByMasterId(Long id);
	
	/**
	 * 
	 * @param uniqueName
	 * @return
	 */
	List<AntibodyMapping> findAntibodyMappingByUniqueName(@ValidEntry String uniqueName);
}
