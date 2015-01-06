package org.nextprot.api.core.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.dao.PeptideMappingDao;
import org.nextprot.api.core.domain.IsoformSpecificity;
import org.nextprot.api.core.domain.PeptideMapping;
import org.nextprot.api.core.domain.PeptideMapping.PeptideEvidence;
import org.nextprot.api.core.service.PeptideMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class PeptideMappingServiceImpl implements PeptideMappingService {

	@Autowired private MasterIdentifierService masterIdentifierService;
	@Autowired private PeptideMappingDao peptideMappingDao;
	
	@Override
	@Cacheable("peptides")
	public List<PeptideMapping> findPeptideMappingByMasterId(Long id) {
		
		List<PeptideMapping> allMapping = this.peptideMappingDao.findPeptidesByMasterId(id);

		// Peptide:Isoform
		Map<String, PeptideMapping> mergeMap = new HashMap<String, PeptideMapping>();

		
		if (allMapping.size() > 0) {
			String key = null;
			List<String> peptideNames = new ArrayList<String>();

			Iterator<IsoformSpecificity> it = null;
			for (PeptideMapping mapping : allMapping) {
				key = mapping.getPeptideUniqueName();

				if (!mergeMap.containsKey(key)) { // not in the map
					peptideNames.add(mapping.getPeptideUniqueName());
					mergeMap.put(key, mapping);
				} else { // already in the map
					it = mapping.getIsoformSpecificity().values().iterator();
					if (it.hasNext())
						mergeMap.get(key).addIsoformSpecificity(it.next());
				}
			}

			List<PeptideEvidence> evidences = this.peptideMappingDao.findPeptideEvidences(peptideNames);

			for (PeptideEvidence evidence : evidences)
				mergeMap.get(evidence.getPeptideName()).addEvidence(evidence);
		}
		return new ArrayList<PeptideMapping>(mergeMap.values());
	}
	
	@Override
	public List<PeptideMapping> findPeptideMappingByUniqueName(String uniqueName) {
		Long masterId = this.masterIdentifierService.findIdByUniqueName(uniqueName);
		return findPeptideMappingByMasterId(masterId);
	}

}
