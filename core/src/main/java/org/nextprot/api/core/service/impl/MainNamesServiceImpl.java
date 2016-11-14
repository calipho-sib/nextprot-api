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

	@Override
	@Cacheable("master-iso-main-names")
	public Map<String, MainNames> findIsoformOrEntryMainName() {
		return mainNamesDAO.getMainNamesMap();
	}

}
