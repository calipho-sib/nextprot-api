package org.nextprot.api.core.service;

import java.util.List;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.annotation.IsoformAnnotation;

public interface EntryModifiedAnnotationService {

	List<IsoformAnnotation> findAnnotationsForModifiedEntry(Entry entry);

}
