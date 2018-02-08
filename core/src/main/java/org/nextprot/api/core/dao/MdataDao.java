package org.nextprot.api.core.dao;

import java.util.List;
import java.util.Map;

import org.nextprot.api.core.domain.Mdata;

public interface MdataDao {

	Map<Long,Long> findEvidenceIdMdataIdMapForPTMsByEntryName(String ac);
	Map<Long,Long> findEvidenceIdMdataIdMapForPeptideMappingsByEntryName(String ac);
	
	List<Mdata> findMdataByIds(List<Long> mdataIds);
	
	List<Long> findExamplesOfEvidencesHavingMdataForNextprotPTMs(int sampleSize);
	List<Long> findExamplesOfEvidencesHavingMdataForNonNextprotPTMs(int sampleSize);
	
}
