package org.nextprot.api.core.dao;

import org.nextprot.api.core.domain.annotation.AnnotationProperty;

import java.util.List;

public interface BioPhyChemPropsDao {

	List<AnnotationProperty> findPropertiesByUniqueName(String uniqueName);
}
