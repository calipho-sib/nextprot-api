package com.nextprot.api.annotation.builder.statement.service;

import java.util.List;

import org.nextprot.api.core.domain.annotation.IsoformAnnotation;

public interface StatementService {
	
	/**
	 * 
	 * @param nextprotAccession can have a dash or not (if not takes all isoforms)
	 * @return
	 */
	List<IsoformAnnotation> getModifiedIsoformAnnotationsByIsoform(String nextprotAccession);

	List<IsoformAnnotation> getNormalAnnotations(String nextprotAccession);

}
