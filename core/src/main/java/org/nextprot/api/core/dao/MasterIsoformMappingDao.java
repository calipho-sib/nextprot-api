package org.nextprot.api.core.dao;

import java.util.List;

import org.nextprot.api.core.domain.TemporaryIsoformSpecificity;

public interface MasterIsoformMappingDao {

	List<TemporaryIsoformSpecificity> findIsoformMappingByMaster(String ac);
	
}
