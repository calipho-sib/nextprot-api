package org.nextprot.api.core.service.impl;

import org.nextprot.api.core.dao.EntryPropertiesDao;
import org.nextprot.api.core.domain.EntryProperties;
import org.nextprot.api.core.service.EntryPropertiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class EntryPropertiesServiceImpl implements EntryPropertiesService {

	@Autowired
	private EntryPropertiesDao entryPropertiesDao;

	@Override
	@Cacheable(value = "entry-properties", sync = true)
	public EntryProperties findEntryProperties(String uniqueName) {

		return entryPropertiesDao.findEntryProperties(uniqueName);
	}
}
