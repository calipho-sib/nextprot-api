package org.nextprot.api.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
	
	@Override
	public List<ExperimentalContext> findExperimentalContextsByIds(Set<Long> ecIds) {
		
		Map<Long,ExperimentalContext> ecMap = ecDico.getIdExperimentalContextMap();
		return ecIds.stream().map(id->ecMap.get(id)).collect(Collectors.toList());
	}

	@Override
	public List<ExperimentalContext> findAllExperimentalContexts() {
		return new ArrayList<ExperimentalContext>(ecDico.getIdExperimentalContextMap().values());
	}

	@Override
	public ExperimentalContext findExperimentalContextByMd5(String md5) {
		//TODO: clear the cache?
		return ecDico.getMd5ExperimentalContextMap().get(md5);
	}

}

