package org.nextprot.api.core.service.impl;

import java.util.List;

import org.nextprot.api.core.dao.MappingReportDAO;
import org.nextprot.api.core.service.MappingReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
class MappingReportServiceImpl implements MappingReportService {

	@Autowired
	private MappingReportDAO mappingReportDao;

	@Override
	@Cacheable(value = "nextprot-hpa-mapping", sync = true)
	public List<String> findHPAMapping() {
		return mappingReportDao.findHpaMapping();
	}

	@Override
	@Cacheable(value = "nextprot-refseq-mapping", sync = true)
	public List<String> findRefSeqMapping() {     
		return mappingReportDao.findRefSeqMapping();
	}

}
