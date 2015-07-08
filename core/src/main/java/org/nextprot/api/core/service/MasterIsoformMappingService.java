package org.nextprot.api.core.service;

import java.util.List;

import org.nextprot.api.core.domain.IsoformSpecificity;

public interface MasterIsoformMappingService {

	List<IsoformSpecificity> findMasterIsoformMappingByEntryName(String uniqueName);

}
