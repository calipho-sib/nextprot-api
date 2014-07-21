package org.nextprot.api.dao;

import java.util.List;

import org.nextprot.api.domain.Identifier;

public interface IdentifierDao {
	
	List<Identifier> findIdentifiersByMaster(String uniqueName);

}
