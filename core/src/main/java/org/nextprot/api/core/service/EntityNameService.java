package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.EntityName;
import org.nextprot.api.core.domain.Overview;

import java.util.List;
import java.util.Map;


public interface EntityNameService {

	/** Check if given entityName has given name */
	boolean hasNameIgnoreCase(EntityName entityName, String name);

    Map<Overview.EntityNameClass, List<EntityName>> findNamesByEntityNameClass(String uniqueName);
}
