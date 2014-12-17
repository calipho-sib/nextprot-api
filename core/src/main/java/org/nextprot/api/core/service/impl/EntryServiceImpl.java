package org.nextprot.api.core.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.nextprot.api.core.dao.EnzymeDao;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.AnnotationService;
import org.nextprot.api.core.service.AntibodyMappingService;
import org.nextprot.api.core.service.DbXrefService;
import org.nextprot.api.core.service.EntryService;
import org.nextprot.api.core.service.ExperimentalContextService;
import org.nextprot.api.core.service.GeneService;
import org.nextprot.api.core.service.GenomicMappingService;
import org.nextprot.api.core.service.IdentifierService;
import org.nextprot.api.core.service.InteractionService;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.service.KeywordService;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.core.service.OverviewService;
import org.nextprot.api.core.service.PeptideMappingService;
import org.nextprot.api.core.service.PublicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EntryServiceImpl implements EntryService {
	@Autowired private OverviewService overviewService;
	@Autowired private PublicationService publicationService;
	@Autowired private DbXrefService xrefService;
	@Autowired private KeywordService kwService;
	@Autowired private IdentifierService identifierService;
	@Autowired private GeneService geneService;
	@Autowired private GenomicMappingService genomicMappingService;
	@Autowired private IsoformService isoformService;
	@Autowired private MasterIdentifierService masterIdentifierService;
	@Autowired private AnnotationService annotationService;
	@Autowired private PeptideMappingService peptideMappingService;
	@Autowired private AntibodyMappingService antibodyMappingService;
	@Autowired private InteractionService interactionService;
	@Autowired private ExperimentalContextService expContextService;
	
	//
	// sorry about breaking the bests practices with spring ;) 
	@Autowired private EnzymeDao enzymeDao;
	
	
	@Override
	public Entry findEntry(String entryName) {
		Entry entry = new Entry(entryName);
		Long masterId = this.masterIdentifierService.findIdByUniqueName(entryName);
		entry.setOverview(this.overviewService.findOverviewByEntry(entryName));
		entry.setEnzymes(enzymeDao.findEnzymeByMaster(entryName));
		entry.setPublications(this.publicationService.findPublicationsByMasterUniqueName(entryName));
		entry.setXrefs(this.xrefService.findDbXrefsByMaster(entryName));
		entry.setKeywords(this.kwService.findKeywordByMaster(entryName));
		entry.setIdentifiers(this.identifierService.findIdentifiersByMaster(entryName));
		entry.setChromosomalLocations(geneService.findChromosomalLocationsByEntry(entryName));
		entry.setGenomicMappings(genomicMappingService.findGenomicMappingsByEntryName(entryName));
		entry.setInteractions(interactionService.findInteractionsByEntry(entryName));
		entry.setIsoforms(this.isoformService.findIsoformsByEntryName(entryName));
		entry.setPeptideMappings(this.peptideMappingService.findPeptideMappingByMasterId(masterId));
		entry.setAntibodyMappings(this.antibodyMappingService.findAntibodyMappingByMasterId(masterId));
		entry.setAnnotations(this.annotationService.findAnnotations(entryName));
		entry.setExperimentalContexts(this.expContextService.findExperimentalContextsByEntryName(entryName));
		return entry;
	}
	
	@Override
	public List<Entry> findEntries(List<String> entryNames) {
		List<Entry> entries = new ArrayList<Entry>();
		for(String entryName : entryNames) entries.add(findEntry(entryName));
		return entries;
	}

	@Override
	public List<Entry> findEntriesByChromossome(String chromossome) {
		List<String> uniqueNames = this.masterIdentifierService.findUniqueNamesOfChromossome(chromossome);
		
		List<Entry> entries = new ArrayList<Entry>();
		
		for(String uniqueName : uniqueNames)  
			entries.add(findEntry(uniqueName));
		return entries;
	}

	@Override
	public List<String> findEntryNamesByChromossome(String chromossome) {
		return this.masterIdentifierService.findUniqueNamesOfChromossome(chromossome);
	}

	

}
