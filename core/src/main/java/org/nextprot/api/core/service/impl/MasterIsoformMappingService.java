package org.nextprot.api.core.service.impl;

import java.util.List;
import java.util.Map;

import org.nextprot.api.core.domain.IsoformSpecificity;

public interface MasterIsoformMappingService {

	Map<String,IsoformSpecificity> findMasterIsoformMappingByEntryName(String uniqueName);

}
