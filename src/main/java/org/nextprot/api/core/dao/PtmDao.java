package org.nextprot.api.core.dao;

import java.util.List;

import org.nextprot.api.core.domain.Feature;

public interface PtmDao {

	List<Feature> findPtmsByEntry(String uniqueName);
	
}
