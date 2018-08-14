package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.EntityName;


public interface EntityNameService {

	/** Check if given entityName has given name */
	boolean hasNameIgnoreCase(EntityName entityName, String name);
}
