package org.nextprot.api.core.dao;

import java.util.List;

import org.nextprot.api.core.domain.PeptideMapping;
import org.nextprot.api.core.domain.PeptideMapping.PeptideEvidence;
import org.nextprot.api.core.domain.PeptideMapping.PeptideProperty;

public interface PeptideMappingDao {

	List<PeptideMapping> findPeptidesByMasterId(Long id);
	
	List<PeptideEvidence> findPeptideEvidences(List<String> names);

	List<PeptideProperty> findPeptideProperties(List<String> names);
	
	
}
