package org.nextprot.api.core.service;

import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.core.utils.graph.CvTermGraph;

public interface CvTermGraphService {

	/**
	 * @return a graph of {@code CvTerm}s of a given terminology
	 */
	CvTermGraph findCvTermGraph(TerminologyCv terminologyCv);
}
