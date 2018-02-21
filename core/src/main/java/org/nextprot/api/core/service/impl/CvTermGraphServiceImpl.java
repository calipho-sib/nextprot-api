package org.nextprot.api.core.service.impl;

import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.core.service.CvTermGraphService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.utils.graph.CvTermGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
class CvTermGraphServiceImpl implements CvTermGraphService {

	@Autowired
	private TerminologyService terminologyService;

	@Override
	@Cacheable("terminology-graph")
	public CvTermGraph findCvTermGraph(TerminologyCv terminologyCv) {

		return new CvTermGraph(terminologyCv, terminologyService);
	}
}
