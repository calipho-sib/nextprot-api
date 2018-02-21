package org.nextprot.api.core.service;

import java.util.List;

import org.nextprot.api.core.domain.annotation.Annotation;

public interface StatementService {

	List<Annotation> getAnnotations(String entryAccession);

}
