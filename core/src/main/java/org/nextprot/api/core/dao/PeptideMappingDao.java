package org.nextprot.api.core.dao;

import java.util.List;

import org.nextprot.api.core.domain.PeptideMapping;
import org.nextprot.api.core.domain.PeptideMapping.PeptideEvidence;
import org.nextprot.api.core.domain.PeptideMapping.PeptideProperty;

public interface PeptideMappingDao {

	List<PeptideMapping> findAllPeptidesByMasterId(Long id);
	List<PeptideMapping> findNaturalPeptidesByMasterId(Long id);
	List<PeptideMapping> findSyntheticPeptidesByMasterId(Long id);
	
	List<PeptideEvidence> findAllPeptideEvidences(List<String> names);
	List<PeptideEvidence> findNaturalPeptideEvidences(List<String> names);
	List<PeptideEvidence> findSyntheticPeptideEvidences(List<String> names);

	List<PeptideProperty> findPeptideProperties(List<String> names);
	
	
}
