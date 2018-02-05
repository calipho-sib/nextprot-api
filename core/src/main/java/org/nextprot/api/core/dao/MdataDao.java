package org.nextprot.api.core.dao;

import java.util.List;

import org.nextprot.api.core.domain.Family;
import org.nextprot.api.core.domain.Mdata;

public interface MdataDao {

	
	List<Mdata> findMdataForNextprotPTMs(List<Long> evidenceIdList);
	List<Long> findExamplesOfEvidencesHavingMdataForNextprotPTMs(int sampleSize);
	
	List<Mdata> findMdataForNonNextprotPTMs(List<Long> evidenceIdList);
	List<Long> findExamplesOfEvidencesHavingMdataForNonNextprotPTMs(int sampleSize);
	
}
