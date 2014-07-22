package org.nextprot.api.service;

import java.util.List;

import org.nextprot.api.domain.AntibodyMapping;
import org.nextprot.api.service.aop.ValidEntry;

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
