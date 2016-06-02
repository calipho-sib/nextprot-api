package org.nextprot.api.core.service;

import java.util.List;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.ModifiedEntry;

public interface EntryModifiedAnnotationService {

	List<ModifiedEntry> findAnnotationsForModifiedEntry(Entry entry);

}
