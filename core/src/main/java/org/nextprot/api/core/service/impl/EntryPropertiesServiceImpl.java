package org.nextprot.api.core.service.impl;

import org.nextprot.api.core.dao.EntryPropertiesDao;
import org.nextprot.api.core.domain.EntryProperties;
import org.nextprot.api.core.domain.EntryReport;
import org.nextprot.api.core.service.EntryPropertiesService;
import org.nextprot.api.core.service.EntryReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class EntryPropertiesServiceImpl implements EntryPropertiesService {

	@Autowired
	private EntryPropertiesDao entryPropertiesDao;

	@Autowired
	private EntryReportService entryReportService;

	@Override
	@Cacheable("entry-properties")
	public EntryProperties findEntryProperties(String uniqueName) {

		EntryProperties entryProperties = entryPropertiesDao.findEntryProperties(uniqueName);

		EntryReport er = entryReportService.reportEntry(uniqueName).get(0);
		entryProperties.setIsoformCount(er.countIsoforms());
		entryProperties.setPtmCount(er.countPTMs());
		entryProperties.setVarCount(er.countVariants());

		return entryProperties;
	}
}
