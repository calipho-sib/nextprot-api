package org.nextprot.api.core.service;

import java.util.List;
import java.util.Map;

import org.nextprot.api.core.domain.Mdata;

/**
 * @author pmichel
 */
public interface MdataService {

	Map<Long,Long> findEvidenceIdMdataIdMapForPTMsByEntryName(String ac);
	List<Mdata> findMdataByIds(List<Long> mdataIds);
	
}
