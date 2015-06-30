package org.nextprot.api.core.dao;

import java.util.List;

import org.nextprot.api.core.domain.Overview.EntityName;

public interface EntityNameDao {

	List<EntityName> findNames(String uniqueName);
	List<EntityName> findAlternativeChainNames(String uniqueName);
	//TODO remove this when orf gene names included in view_master_identifier_names
	@Deprecated
	List<EntityName> findORFGeneNames(String uniqueName);

}
