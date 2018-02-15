package org.nextprot.api.core.service.impl;

import org.nextprot.api.core.dao.EntryPropertiesDao;
import org.nextprot.api.core.domain.EntryProperties;
import org.nextprot.api.core.domain.EntryReportStats;
import org.nextprot.api.core.service.EntryPropertiesService;
import org.nextprot.api.core.service.EntryReportStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class EntryPropertiesServiceImpl implements EntryPropertiesService {

	@Autowired
	private EntryPropertiesDao entryPropertiesDao;

	@Autowired
	private EntryReportStatsService entryReportStatsService;

	@Override
	@Cacheable("entry-properties")
	public EntryProperties findEntryProperties(String uniqueName) {

		EntryProperties entryProperties = entryPropertiesDao.findEntryProperties(uniqueName);

		EntryReportStats ers = entryReportStatsService.reportEntryStats(uniqueName);
		entryProperties.setIsoformCount(ers.countIsoforms());
		entryProperties.setPtmCount(ers.countPTMs());
		entryProperties.setVarCount(ers.countVariants());

		return entryProperties;
	}
}
