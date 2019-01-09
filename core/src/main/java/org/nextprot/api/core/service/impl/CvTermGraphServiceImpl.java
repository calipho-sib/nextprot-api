package org.nextprot.api.core.service.impl;

import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.core.domain.CvTermGraph;
import org.nextprot.api.core.service.CvTermGraphService;
import org.nextprot.api.core.service.TerminologyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
class CvTermGraphServiceImpl implements CvTermGraphService {

	@Autowired
	private TerminologyService terminologyService;

	@Override
	@Cacheable(value = "terminology-graph", sync = true)
	public CvTermGraph findCvTermGraph(TerminologyCv terminologyCv) {

		return new CvTermGraph(terminologyCv, terminologyService.findCvTermsByOntology(terminologyCv.name()));
	}
}
