package org.nextprot.api.core.dao;

import org.nextprot.api.core.domain.EntityName;

import java.util.List;

public interface EntityNameDao {

	List<EntityName> findNames(String uniqueName);
	List<EntityName> findAlternativeChainNames(String uniqueName);

}
