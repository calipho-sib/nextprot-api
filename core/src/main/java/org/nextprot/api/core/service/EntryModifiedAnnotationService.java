package org.nextprot.api.core.service;

import java.util.List;

import org.nextprot.api.core.domain.ModifiedEntry;
import org.nextprot.api.core.service.annotation.ValidEntry;


public interface EntryModifiedAnnotationService {

	List<ModifiedEntry> findAnnotationsForModifiedEntry(@ValidEntry String entryName);

}
