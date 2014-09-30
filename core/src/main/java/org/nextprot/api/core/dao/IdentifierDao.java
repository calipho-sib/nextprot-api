package org.nextprot.api.core.dao;

import java.util.List;

import org.nextprot.api.core.domain.Identifier;

public interface IdentifierDao {
	
	List<Identifier> findIdentifiersByMaster(String uniqueName);

}
