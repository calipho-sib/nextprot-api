package org.nextprot.api.core.service.impl;

import java.util.Map;

import org.nextprot.api.core.dao.MainNamesDAO;
import org.nextprot.api.core.domain.MainNames;
import org.nextprot.api.core.service.MainNamesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
class MainNamesServiceImpl implements MainNamesService {

	@Autowired private MainNamesDAO mainNamesDAO;

	// Note: because this service is called many many times and executes in more than 500ms 
	// when cache is OFF, some tests performed by jenkins last more than 1 hour !
	// So instead of using ehcache, data is setup the first time
	// I know this not good practice but doing it the spring way is possible, 
	// see https://www.mkyong.com/spring/spring-postconstruct-and-predestroy-example/
	// Two problems may arise with this implementation:
	// - two threads calling the service at the same time when data is still null or not fully set (low probability)
	// - some thread modifies the content of data (fortunately we're not idiot)
	
	private Map<String, MainNames> data;
		
	@Override
	public Map<String, MainNames> findIsoformOrEntryMainName() {
		if (data==null) data=mainNamesDAO.getMainNamesMap();
		return data;
	}
	
}
