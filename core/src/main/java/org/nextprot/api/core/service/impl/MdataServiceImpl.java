package org.nextprot.api.core.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nextprot.api.core.dao.MdataDao;
import org.nextprot.api.core.domain.Mdata;
import org.nextprot.api.core.service.MdataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
class MdataServiceImpl implements MdataService {

	@Autowired private MdataDao mdataDao;

	@Cacheable("evidence-mdata-map-by-entry")
	@Override
	public Map<Long, Long> findEvidenceIdMdataIdMapByEntryName(String ac) {
		
		Map<Long,Long> eviMdataMap = new HashMap<>();
		eviMdataMap.putAll(mdataDao.findEvidenceIdMdataIdMapForPTMsByEntryName(ac));
		eviMdataMap.putAll(mdataDao.findEvidenceIdMdataIdMapForPeptideMappingsByEntryName(ac));
		return eviMdataMap;
	}

	@Cacheable("mdata-list-by-entry")
	@Override
	public List<Mdata> findMdataByIds(List<Long> mdataIds) {
		return mdataDao.findMdataByIds(mdataIds);
	}

}
