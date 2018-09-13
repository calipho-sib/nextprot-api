package org.nextprot.api.core.dao;

import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.List;

public interface AntibodyMappingDao {

	List<Annotation> findAntibodyMappingAnnotationsById(long masterId);

	List<String> findAntibodyIsoformMappingsList();
}
