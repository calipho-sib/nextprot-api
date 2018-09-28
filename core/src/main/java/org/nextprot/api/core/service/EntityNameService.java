package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.EntityName;
import org.nextprot.api.core.domain.Overview;

import java.util.List;


public interface EntityNameService {

	/** Check if given entityName has given name */
	boolean hasNameIgnoreCase(EntityName entityName, String name);

    List<EntityName> findNamesByEntityNameClass(String uniqueName, Overview.EntityNameClass entityNameClass);
}
