package org.nextprot.api.core.dao;

import java.util.List;

import org.nextprot.api.core.domain.CvDatabase;

public interface SimpleDao {
	
	List<CvDatabase> findAllCvDatabases();
	
}
