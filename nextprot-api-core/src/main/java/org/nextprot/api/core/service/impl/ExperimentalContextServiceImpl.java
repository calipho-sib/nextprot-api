package org.nextprot.api.core.service.impl;

import java.util.List;

import org.nextprot.api.core.dao.ExperimentalContextDao;
import org.nextprot.api.core.domain.ExperimentalContext;
import org.nextprot.api.core.service.ExperimentalContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class ExperimentalContextServiceImpl implements ExperimentalContextService {
	
	@Autowired ExperimentalContextDao ecDao;
	
	@Override
	public List<ExperimentalContext> findExperimentalContextsByIds(List<String> ids) {
		return ecDao.findExperimentalContextsByIds(ids);
	}

	@Override
	public List<ExperimentalContext> findAllExperimentalContexts() {
		return ecDao.findAllExperimentalContexts();
	}
		
}
