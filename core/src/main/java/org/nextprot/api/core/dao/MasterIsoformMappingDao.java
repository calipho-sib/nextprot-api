package org.nextprot.api.core.dao;

import java.util.List;

import org.nextprot.api.core.domain.IsoformSpecificity;

public interface MasterIsoformMappingDao {

	List<IsoformSpecificity> findIsoformMappingByMaster(String ac);
	
}
