package org.nextprot.api.core.service;

import java.util.List;

import org.nextprot.api.core.domain.ExperimentalContext;
import org.nextprot.api.core.domain.annotation.Annotation;

public interface ExperimentalContextService {

	List<ExperimentalContext> findExperimentalContextsByAnnotations(List<Annotation> annotations);
	List<ExperimentalContext> findAllExperimentalContexts();

}
