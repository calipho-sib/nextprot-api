package com.nextprot.api.annotation.builder.statement.service;

import java.util.List;

import org.nextprot.api.core.domain.annotation.Annotation;

public interface StatementService {

	List<Annotation> getAnnotations(String entryAccession);
	//List<IsoformAnnotation> getIsoformAnnotations(String entryAccession);

}
