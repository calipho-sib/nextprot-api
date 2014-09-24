package org.nextprot.api.core.dao;

import java.util.List;

import org.nextprot.api.rdf.domain.Terminology;

public interface EnzymeDao {

	List<Terminology> findEnzymeByMaster(String uniqueName);
}
