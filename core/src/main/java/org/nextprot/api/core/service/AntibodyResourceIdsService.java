package org.nextprot.api.core.service;

import java.util.List;

public interface AntibodyResourceIdsService {
	
	List<Long> findAllAntibodyIdsByMasterId(String uniqueName);

}
