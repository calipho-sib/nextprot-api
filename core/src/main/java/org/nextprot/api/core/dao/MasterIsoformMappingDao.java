package org.nextprot.api.core.dao;

import org.nextprot.api.core.domain.IsoformSpecificity;

import java.util.List;

public interface MasterIsoformMappingDao {

	List<IsoformSpecificity> findIsoformMappingByMaster(String ac);
}
