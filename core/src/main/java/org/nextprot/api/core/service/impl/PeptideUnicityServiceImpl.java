package org.nextprot.api.core.service.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.nextprot.api.core.dao.PeptideMappingDao;
import org.nextprot.api.core.domain.PeptideUnicity;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.service.PeptideUnicityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;




@Service
class PeptideUnicityServiceImpl implements PeptideUnicityService {

	@Autowired private IsoformService isoService;
	@Autowired private PeptideMappingDao peptideMappingDao;
	
    private static final Logger LOGGER = Logger.getLogger(PeptideUnicityServiceImpl.class);
	
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

		LOGGER.info("Starting, thread: " + Thread.currentThread().getId());

		Map<String,PeptideUnicity> result = new HashMap<>();
		List<String> list = peptideMappingDao.findPeptideIsoformMappingsList();

		LOGGER.info("Got mapping iso-pep from db, size: " + list.size() + " , thread: " + Thread.currentThread().getId());

		for (int i=0;i<list.size();i++) {
			String row = list.get(i);
			String[] fields = row.split(":");
			String pep = fields[0];
			String[] isolist = fields[1].split(",");
			Set<String> mappedIsoSet = new HashSet<>(Arrays.asList(isolist));
			PeptideUnicity pu = getPeptideUnicityFromMappingIsoforms(mappedIsoSet);
			result.put(pep, pu);
		}
		
		LOGGER.info("Computed uncity for peptides, size: " + result.size() + " , thread: " + Thread.currentThread().getId());
		LOGGER.info("Done" + " , thread: " + Thread.currentThread().getId());
		
		return result;
	}
	

	
}
