package com.nextprot.api.annotation.builder.statement.service;

import java.util.List;

import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.IsoformAnnotation;

public interface StatementService {
	
	List<Annotation> getProteoformEntryAnnotations(String entryAccession);

	List<Annotation> getNormalEntryAnnotations(String entryAccession);

	List<IsoformAnnotation> getProteoformIsoformAnnotations(String isoformAccession);

	List<IsoformAnnotation> getNormalIsoformAnnotations(String isoformAccession);

}
