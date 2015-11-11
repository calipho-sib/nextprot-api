package org.nextprot.api.core.dao;

import java.util.List;

public interface EntityNameDao {

	List<EntityName> findNames(String uniqueName);
	List<EntityName> findAlternativeChainNames(String uniqueName);

}
