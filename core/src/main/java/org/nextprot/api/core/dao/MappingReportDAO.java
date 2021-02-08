package org.nextprot.api.core.dao;

import java.util.List;

public interface MappingReportDAO {

	public List<String> findHpaMapping();
	public List<String> findRefSeqMapping();

}
