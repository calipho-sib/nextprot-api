package org.nextprot.api.core.service.impl;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.nextprot.api.core.domain.PeptideUnicity;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.service.PeptideUnicityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
class PeptideUnicityServiceImpl implements PeptideUnicityService {

	@Autowired private IsoformService isoService;
	
	
	@Override
	public PeptideUnicity getPeptideUnicityFormMappingIsoforms(Set<String> isoformAcs) {
		
		Set<String> entryAcs = new TreeSet<>();
		isoformAcs.stream().forEach(ac -> entryAcs.add(ac.split("-")[0]));
		
		// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
		// CASE 1 - matches some isoform(s) of a single entry
		// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 		
		if (entryAcs.size()==1) {
			return PeptideUnicity.createPeptideUnicityUnique();  
		}
		// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
		// CASE 2 - matches some isoforms of entries sharing an equivalent isoform
		// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
		else if (isoService.getSetsOfEntriesHavingAnEquivalentIsoform().stream().anyMatch(s -> s.equals(entryAcs))) {
			List<Set<String>> includedSets = isoService.getSetsOfEquivalentIsoforms().stream()
					.filter(s -> isoformAcs.containsAll(s)).collect(Collectors.toList());
			if (includedSets.size()!=1) throw new RuntimeException();
			return PeptideUnicity.createPeptideUnicityPseudoUnique(includedSets.get(0));
				
		}	
		// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
		// CASE 3 - matches isoforms of several entries not sharing any equivalent isoform	
		// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
		else {
			return PeptideUnicity.createPeptideUnicityNonUnique();
		}
	}
	

	
}
