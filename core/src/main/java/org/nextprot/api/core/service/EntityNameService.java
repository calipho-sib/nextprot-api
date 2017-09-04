package org.nextprot.api.core.service;

import org.nextprot.api.core.dao.EntityName;


public interface EntityNameService {

	/** Check if given entityName has given name */
	boolean hasName(EntityName entityName, String name);
}
