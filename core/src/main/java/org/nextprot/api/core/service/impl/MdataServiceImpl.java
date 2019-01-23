package org.nextprot.api.core.service.impl;

import org.nextprot.api.core.dao.MdataDao;
import org.nextprot.api.core.domain.Mdata;
import org.nextprot.api.core.service.MdataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
class MdataServiceImpl implements MdataService {

	@Autowired private MdataDao mdataDao;

	@Cacheable(value = "evidence-mdata-map-by-entry", sync = true)
	@Override
	public Map<Long, Long> findEvidenceIdMdataIdMapByEntryName(String ac) {
		
		Map<Long,Long> eviMdataMap = new HashMap<>();
		eviMdataMap.putAll(mdataDao.findEvidenceIdMdataIdMapForPTMsByEntryName(ac));
		eviMdataMap.putAll(mdataDao.findEvidenceIdMdataIdMapForPeptideMappingsByEntryName(ac));
		return eviMdataMap;
	}

	@Cacheable(value = "mdata-list-by-entry", sync = true)
	@Override
	public List<Mdata> findMdataByIds(List<Long> mdataIds) {
		return mdataDao.findMdataByIds(mdataIds);
	}

}
