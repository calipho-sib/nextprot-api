package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.IsoformSpecificity;

import java.util.List;

public interface MasterIsoformMappingService {

	List<IsoformSpecificity> findMasterIsoformMappingByEntryName(String uniqueName);
}
