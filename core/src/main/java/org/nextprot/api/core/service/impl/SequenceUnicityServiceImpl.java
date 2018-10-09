package org.nextprot.api.core.service.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.nextprot.api.core.dao.AntibodyMappingDao;
import org.nextprot.api.core.dao.PeptideMappingDao;
import org.nextprot.api.core.domain.SequenceUnicity;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.service.SequenceUnicityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


@Service
class SequenceUnicityServiceImpl implements SequenceUnicityService {

	@Autowired private IsoformService isoService;
	@Autowired private PeptideMappingDao peptideMappingDao;
	@Autowired private AntibodyMappingDao antibodyMappingDao;

	private static final Logger LOGGER = Logger.getLogger(SequenceUnicityServiceImpl.class);
	
	@Override
	public SequenceUnicity getSequenceUnicityFromMappingIsoforms(Set<String> mappingIsoforms) {
		
		Set<String> entryAcs = new TreeSet<>();
		mappingIsoforms.forEach(ac -> entryAcs.add(ac.split("-")[0]));
		
		// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
		// CASE 1 - matches isoform(s) of a single entry
		// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 		
		if (entryAcs.size()==1) {
			return SequenceUnicity.createSequenceUnicityUnique();
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
				return SequenceUnicity.createSequenceUnicityPseudoUnique(equivalentIsoSet);
			}	
			
			// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
			// CASE 3 - matches isoforms of entries not all sharing an equivalent isoform	
			// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
			else {
				return SequenceUnicity.createSequenceUnicityNonUnique(equivalentIsoSet);
			}
		}
	}

	@Override
	@Cacheable("peptide-name-unicity-map")
	public Map<String, SequenceUnicity> getPeptideNameUnicityMap() {

		LOGGER.info("Starting, thread: " + Thread.currentThread().getId());

		List<String> list = peptideMappingDao.findPeptideIsoformMappingsList();

		LOGGER.info("Got mapping iso-pep from db, size: " + list.size() + " , thread: " + Thread.currentThread().getId());

		Map<String, SequenceUnicity> result = getUnicityMap(list);

		LOGGER.info("Computed unicity for peptides, size: " + result.size() + " , thread: " + Thread.currentThread().getId());
		LOGGER.info("Done" + " , thread: " + Thread.currentThread().getId());
		
		return result;
	}

	@Override
	@Cacheable("antibody-name-unicity-map")
	public Map<String, SequenceUnicity> getAntibodyNameUnicityMap() {
		LOGGER.info("Starting, thread: " + Thread.currentThread().getId());

		List<String> list = antibodyMappingDao.findAntibodyIsoformMappingsList();

		LOGGER.info("Got mapping iso-ab from db, size: " + list.size() + " , thread: " + Thread.currentThread().getId());

		Map<String, SequenceUnicity> result = getUnicityMap(list);

		LOGGER.info("Computed unicity for antibodies, size: " + result.size() + " , thread: " + Thread.currentThread().getId());
		LOGGER.info("Done" + " , thread: " + Thread.currentThread().getId());

		return result;
	}

	private Map<String, SequenceUnicity> getUnicityMap(List<String> list) {
		Map<String, SequenceUnicity> result = new HashMap<>();

		for (String row : list) {
			String[] fields = row.split(":");
			String pep = fields[0];
			String[] isolist = fields[1].split(",");
			Set<String> mappedIsoSet = Arrays.stream(isolist).collect(Collectors.toSet());
			SequenceUnicity pu = getSequenceUnicityFromMappingIsoforms(mappedIsoSet);
			result.put(pep, pu);
		}
		return result;
	}
}
