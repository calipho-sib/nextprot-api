package org.nextprot.api.core.service.impl;

import java.util.List;

import org.nextprot.api.core.domain.TemporaryIsoformSpecificity;

public interface MasterIsoformMappingService {

	List<TemporaryIsoformSpecificity> findMasterIsoformMappingByEntryName(String uniqueName);

}
