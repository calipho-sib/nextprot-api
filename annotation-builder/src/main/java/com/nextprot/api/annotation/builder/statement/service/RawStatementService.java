package com.nextprot.api.annotation.builder.statement.service;

import java.util.List;

import org.nextprot.api.core.domain.ModifiedEntry;
import org.nextprot.api.core.domain.annotation.Annotation;

public interface RawStatementService {

	public List<ModifiedEntry> getModifiedEntryAnnotation(String entryName);
	public List<Annotation> getNormalAnnotations(String entryName);

}
