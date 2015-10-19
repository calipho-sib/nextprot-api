package org.nextprot.api.core.dao;

import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;

import java.util.List;

public interface MasterIsoformMappingDao {

	List<AnnotationIsoformSpecificity> findIsoformMappingByMaster(String ac);
	
}
