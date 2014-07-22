package org.nextprot.api.dao;

import java.util.List;

import org.nextprot.rdf.domain.Terminology;

public interface EnzymeDao {

	List<Terminology> findEnzymeByMaster(String uniqueName);
}
