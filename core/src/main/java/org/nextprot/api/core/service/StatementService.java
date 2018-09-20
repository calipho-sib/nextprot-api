package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.List;
import java.util.Set;

public interface StatementService {

	List<Annotation> getAnnotations(String entryAccession);
    Set<DbXref> findDbXrefs(String entryAccession);
}
