package org.nextprot.api.dao;

import java.util.List;

import org.nextprot.api.domain.Family;

public interface FamilyDao {

	List<Family> findFamilies(String uniqueName);
}
