package org.nextprot.api.core.dao;

import java.util.List;

import org.nextprot.api.core.domain.Family;

public interface FamilyDao {

	List<Family> findFamilies(String uniqueName);
}
