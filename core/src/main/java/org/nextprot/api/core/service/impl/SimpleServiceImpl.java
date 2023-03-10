package org.nextprot.api.core.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nextprot.api.core.dao.SimpleDao;
import org.nextprot.api.core.domain.CvDatabase;
import org.nextprot.api.core.service.SimpleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;


@Lazy
@Service
class SimpleServiceImpl implements SimpleService {

	@Autowired private SimpleDao simpleDao;

	@Override
	@Cacheable(value = "name-database-map", sync = true)
	public Map<String, CvDatabase> getNameDatabaseMap() {
		List<CvDatabase> dbList = simpleDao.findAllCvDatabases();
		Map<String, CvDatabase> map = new HashMap<>();
		for (CvDatabase db: dbList) map.put(db.getName(), db);
		return map;
	}


}
