package org.nextprot.api.core.dao;

import org.nextprot.api.commons.utils.NucleotidePositionRange;
import org.nextprot.api.core.domain.IsoformSpecificity;

import java.util.List;
import java.util.Map;

public interface MasterIsoformMappingDao {

	List<IsoformSpecificity> findIsoformMappingByMaster(String ac);

	Map<String, List<NucleotidePositionRange>> findMasterIsoformMapping(String ac);
}
