package org.nextprot.api.dao;

import java.util.List;

import org.nextprot.api.domain.Overview.EntityName;

public interface EntityNameDao {

	List<EntityName> findNames(String uniqueName);
}
