package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;

import java.util.List;

public interface MasterIsoformMappingService {

	List<AnnotationIsoformSpecificity> findMasterIsoformMappingByEntryName(String uniqueName);

}
