package org.nextprot.api.core.service.impl;

import org.nextprot.api.core.dao.PeptideMappingDao;
import org.nextprot.api.core.domain.PeptideUnicity;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.service.PeptideUnicityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;



@Service
class PeptideUnicityServiceImpl implements PeptideUnicityService {

	@Autowired private IsoformService isoService;
	@Autowired private PeptideMappingDao peptideMappingDao;
	
	
	@Override
	public PeptideUnicity getPeptideUnicityFromMappingIsoforms(Set<String> mappingIsoforms) {
		
		Set<String> entryAcs = new TreeSet<>();
		mappingIsoforms.stream().forEach(ac -> entryAcs.add(ac.split("-")[0]));
		
		// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
		// CASE 1 - matches isoform(s) of a single entry
		// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 		
		if (entryAcs.size()==1) {
			return PeptideUnicity.createPeptideUnicityUnique();  
		} 
		
		else {
			
			// get list of isoforms known as equivalent which are included in the mapping isoforms
			List<Set<String>> equivalentIsoformsList = isoService.getSetsOfEquivalentIsoforms().stream()
				.filter(s -> mappingIsoforms.containsAll(s)).collect(Collectors.toList());
			Set<String> equivalentIsoSet = null;
			// we just keep the first set of equivalent isoforms
			// the probability that a peptide matches 2 isoforms belonging to 2 distinct equivalence set is really weeaaak
			if (equivalentIsoformsList.size()>0) 
				equivalentIsoSet = equivalentIsoformsList.get(0);
			
			// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
			// CASE 2 - matches isoforms of entries sharing an equivalent isoform
			// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
			if (isoService.getSetsOfEntriesHavingAnEquivalentIsoform().stream().anyMatch(s -> s.equals(entryAcs))) {
				return PeptideUnicity.createPeptideUnicityPseudoUnique(equivalentIsoSet);					
			}	
			
			// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
			// CASE 3 - matches isoforms of entries not all sharing an equivalent isoform	
			// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
			else {
				return PeptideUnicity.createPeptideUnicityNonUnique(equivalentIsoSet);
			}
		}
	}


	@Override
	@Cacheable("peptide-name-unicity-map")
	public Map<String,PeptideUnicity> getPeptideNameUnicityMap() {

		Map<String,PeptideUnicity> result = new HashMap<>();
		System.out.println("" + new Date() + "PeptideUnicityService building cache...");
		List<String> list = peptideMappingDao.findPeptideIsoformMappingsList();
		System.out.println("list size:" + list.size());
		for (int i=0;i<list.size();i++) {
			String row = list.get(i);
			String[] fields = row.split(":");
			String pep = fields[0];
			String[] isolist = fields[1].split(",");
			Set<String> mappedIsoSet = new HashSet<>(Arrays.asList(isolist));
			PeptideUnicity pu = getPeptideUnicityFromMappingIsoforms(mappedIsoSet);
			result.put(pep, pu);
		}
		System.out.println("size:" + result.size());
		System.out.println("" + new Date() + "PeptideUnicityService building cache... done");
		return result;
	}
	

	
}
