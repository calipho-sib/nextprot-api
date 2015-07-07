package org.nextprot.api.core.service;

import java.util.List;

import org.nextprot.api.core.domain.AntibodyMapping;
import org.nextprot.api.core.service.annotation.ValidEntry;

public interface AntibodyMappingService {

	/**
	 * 
	 * @param uniqueName
	 * @return
	 */
	List<AntibodyMapping> findAntibodyMappingByUniqueName(@ValidEntry String uniqueName);
}
