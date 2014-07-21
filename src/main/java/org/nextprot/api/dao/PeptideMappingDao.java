package org.nextprot.api.dao;

import java.util.List;

import org.nextprot.api.domain.PeptideMapping;
import org.nextprot.api.domain.PeptideMapping.PeptideEvidence;

public interface PeptideMappingDao {

	List<PeptideMapping> findPeptidesByMasterId(Long id);
	
	List<PeptideEvidence> findPeptideEvidences(List<String> names);
}
