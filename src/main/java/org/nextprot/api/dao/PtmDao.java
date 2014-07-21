package org.nextprot.api.dao;

import java.util.List;

import org.nextprot.api.domain.Feature;

public interface PtmDao {

	List<Feature> findPtmsByEntry(String uniqueName);
	
}
