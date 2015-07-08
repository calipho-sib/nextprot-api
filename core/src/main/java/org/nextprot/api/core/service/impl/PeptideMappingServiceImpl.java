package org.nextprot.api.core.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.dao.PeptideMappingDao;
import org.nextprot.api.core.domain.IsoformSpecificity;
import org.nextprot.api.core.domain.PeptideMapping;
import org.nextprot.api.core.domain.PeptideMapping.PeptideEvidence;
import org.nextprot.api.core.domain.PeptideMapping.PeptideProperty;
import org.nextprot.api.core.service.PeptideMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableList;

@Service
public class PeptideMappingServiceImpl implements PeptideMappingService {

	@Autowired private MasterIdentifierService masterIdentifierService;
	@Autowired private PeptideMappingDao peptideMappingDao;

	
	@Override
	@Cacheable("natural-peptides")
	public List<PeptideMapping> findNaturalPeptideMappingByMasterUniqueName(String uniqueName) {
		
		Long masterId = this.masterIdentifierService.findIdByUniqueName(uniqueName);
		List<PeptideMapping> peps =  findPeptideMappingByMasterId(masterId, true);
		
		//returns a immutable list when the result is cacheable (this prevents modifying the cache, since the cache returns a reference) copy on read and copy on write is too much time consuming
		return new ImmutableList.Builder<PeptideMapping>().addAll(peps).build();



	}
	
	@Override
	@Cacheable("srm-peptides")
	public List<PeptideMapping> findSyntheticPeptideMappingByMasterUniqueName(String uniqueName) {

		Long masterId = this.masterIdentifierService.findIdByUniqueName(uniqueName);
		List<PeptideMapping> peps = findPeptideMappingByMasterId(masterId, false);

		//returns a immutable list when the result is cacheable (this prevents modifying the cache, since the cache returns a reference) copy on read and copy on write is too much time consuming
		return new ImmutableList.Builder<PeptideMapping>().addAll(peps).build();

	}
		
	@Override
	@Cacheable("all-peptides")
	public List<String> findAllPeptideNamesByMasterId(String uniqueName) {
		Long masterId = this.masterIdentifierService.findIdByUniqueName(uniqueName);
		List<PeptideMapping> allMapping = this.peptideMappingDao.findAllPeptidesByMasterId(masterId);
		Set<String> names = new HashSet<String>(); 
		for (PeptideMapping map: allMapping) names.add(map.getPeptideUniqueName());
		//returns a immutable list when the result is cacheable (this prevents modifying the cache, since the cache returns a reference) copy on read and copy on write is too much time consuming
		return new ImmutableList.Builder<String>().addAll(new ArrayList<String>(names)).build();
	}

	
	private List<PeptideMapping> findPeptideMappingByMasterId(Long id, boolean isNatural) {
		
		List<PeptideMapping> allMapping = isNatural ? 
			this.peptideMappingDao.findNaturalPeptidesByMasterId(id) :
			this.peptideMappingDao.findSyntheticPeptidesByMasterId(id) ;
		
		// key=peptide,value=mapping with 1-n isospecs, 1-n evidences, 1-n properties
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

			// attach evidences to peptide mappings
			List<PeptideEvidence> evidences = isNatural ?
				this.peptideMappingDao.findNaturalPeptideEvidences(peptideNames) :
				this.peptideMappingDao.findSyntheticPeptideEvidences(peptideNames);
			for (PeptideEvidence evidence : evidences)
				mergeMap.get(evidence.getPeptideName()).addEvidence(evidence);
			
			// attach properties to peptide mappings
			List<PeptideProperty> props = this.peptideMappingDao.findPeptideProperties(peptideNames);
			for (PeptideProperty prop: props) 
				mergeMap.get(prop.getPeptideName()).addProperty(prop);
		}
		
		return new ArrayList<PeptideMapping>(mergeMap.values());
	}

	
}
