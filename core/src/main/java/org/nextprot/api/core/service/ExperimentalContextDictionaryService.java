package org.nextprot.api.core.service;

import java.util.Map;

import org.nextprot.api.core.domain.ExperimentalContext;

public interface ExperimentalContextDictionaryService {

	Map<Long,ExperimentalContext> getIdExperimentalContextMap();
	Map<String,ExperimentalContext> getMd5ExperimentalContextMap();
}
