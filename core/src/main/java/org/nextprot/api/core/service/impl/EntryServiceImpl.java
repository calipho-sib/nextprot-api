package org.nextprot.api.core.service.impl;

import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.EntryService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
class EntryServiceImpl implements EntryService {

	
	@Autowired EntryBuilderService entryBuilderService;
	@Autowired MasterIdentifierService masterIdentifierService;

	@Override
	public Entry findEntry(String entryName) {
		return entryBuilderService.build(EntryConfig.newConfig(entryName).withEverything());
	}
	
	@Override
	public List<Entry> findEntries(List<String> entryNames) {
		List<Entry> entries = new ArrayList<Entry>();
		for(String entryName : entryNames) entries.add(findEntry(entryName));
		return entries;
	}

	@Override
	public List<Entry> findEntriesByChromosome(String chromosome) {
		List<String> uniqueNames = this.masterIdentifierService.findUniqueNamesOfChromosome(chromosome);
		
		List<Entry> entries = new ArrayList<Entry>();
		
		for(String uniqueName : uniqueNames)  
			entries.add(findEntry(uniqueName));
		return entries;
	}

	@Override
	public List<String> findEntryNamesByChromosome(String chromosome) {
		return this.masterIdentifierService.findUniqueNamesOfChromosome(chromosome);
	}


}
