package org.nextprot.api.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.nextprot.api.core.dao.ExperimentalContextDao;
import org.nextprot.api.core.domain.ExperimentalContext;
import org.nextprot.api.core.service.ExperimentalContextDictionaryService;
import org.nextprot.api.core.service.ExperimentalContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;


@Lazy
@Service
class ExperimentalContextServiceImpl implements ExperimentalContextService {
	
	@Autowired
	private ExperimentalContextDictionaryService ecDico;

	@Autowired
	private ExperimentalContextDao ecDao;
	
	
	@Override
	public List<ExperimentalContext> findExperimentalContextsByIds(Set<Long> ecIds) {
		
		Map<Long,ExperimentalContext> ecMap = ecDico.getAllExperimentalContexts();
		return ecIds.stream().map(id->ecMap.get(id)).collect(Collectors.toList());
	}

	@Override
	public List<ExperimentalContext> findAllExperimentalContexts() {
		
		return new ArrayList<ExperimentalContext>(ecDico.getAllExperimentalContexts().values());
	}

	@Override
	public ExperimentalContext findExperimentalContextByProperties(long tissueId, long developmentalStageId, long detectionMethodId) {

		return null;
	}

}

