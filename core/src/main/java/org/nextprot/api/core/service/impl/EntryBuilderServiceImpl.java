package org.nextprot.api.core.service.impl;

import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.EntryUtils;
import org.nextprot.api.core.service.AnnotationService;
import org.nextprot.api.core.service.AntibodyMappingService;
import org.nextprot.api.core.service.DbXrefService;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.ExperimentalContextService;
import org.nextprot.api.core.service.GeneService;
import org.nextprot.api.core.service.GenomicMappingService;
import org.nextprot.api.core.service.IdentifierService;
import org.nextprot.api.core.service.InteractionService;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.service.KeywordService;
import org.nextprot.api.core.service.OverviewService;
import org.nextprot.api.core.service.PeptideMappingService;
import org.nextprot.api.core.service.PublicationService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EntryBuilderServiceImpl implements EntryBuilderService{

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
	@Autowired private ExperimentalContextService experimentalContextService;
	@Autowired private TerminologyService terminologyService; //TODO shouldn't we have method in entry to get the enzymes based on the EC names???

	@Override
	public Entry build(EntryConfig entryConfig) {
	
		String entryName = entryConfig.getEntryName();
		Entry entry = new Entry(entryName);

//		synchronized (){

			if(entryConfig.hasOverview()){
				entry.setOverview(this.overviewService.findOverviewByEntry(entryName));
			}
			if(entryConfig.hasPublications()){
				entry.setPublications(this.publicationService.findPublicationsByMasterUniqueName(entryName));
			}
			if(entryConfig.hasXrefs()){
				entry.setXrefs(this.xrefService.findDbXrefsByMaster(entryName));
			}
			if(entryConfig.hasIdentifiers()){
				entry.setIdentifiers(this.identifierService.findIdentifiersByMaster(entryName));
			}
			if(entryConfig.hasChromosomalLocations()){
				entry.setChromosomalLocations(this.geneService.findChromosomalLocationsByEntry(entryName));
			}
			if(entryConfig.hasGenomicMappings()){
				entry.setGenomicMappings(this.genomicMappingService.findGenomicMappingsByEntryName(entryName));
			}
			if(entryConfig.hasTargetIsoforms()){
				entry.setIsoforms(this.isoformService.findIsoformsByEntryName(entryName));
			}
			if(entryConfig.hasGeneralAnnotations()){
				entry.setAnnotations(this.annotationService.findAnnotations(entryName));
			}
			if(entryConfig.hasAntibodyMappings()){
				entry.setAntibodyMappings(this.antibodyMappingService.findAntibodyMappingByUniqueName(entryName));
			}
			if(entryConfig.hasPeptideMappings()){
				entry.setPeptideMappings(this.peptideMappingService.findNaturalPeptideMappingByMasterUniqueName(entryName));
			}
			if(entryConfig.hasSrmPeptideMappings()){
				entry.setSrmPeptideMappings(this.peptideMappingService.findSyntheticPeptideMappingByMasterUniqueName(entryName));
			}
			if(entryConfig.hasExperimentalContext()){
				entry.setExperimentalContexts(this.experimentalContextService.findExperimentalContextsByEntryName(entryName));
			}
			if(entryConfig.hasEnzymes()){
				entry.setEnzymes(terminologyService.findEnzymeByMaster(entryName));
			}
			
			if(entryConfig.hasGeneralAnnotations() || entryConfig.hasSubPart()){ //TODO should be added in annotation list
						setEntryAdditionalInformation(entry); //adds isoforms, publications, xrefs and experimental contexts
			} 

//		}
		
		//CPU Intensive
		if(entryConfig.hasSubPart()){ //TODO should be added in annotation list
			
			if(entryConfig.hasSubPart()){
				return EntryUtils.filterEntryBySubPart(entry, entryConfig.getSubpart());
			}else return entry;
			
		} else {
			return entry;
		}

	}
	
	private void setEntryAdditionalInformation(Entry entry){
		if(entry.getAnnotations() == null || entry.getAnnotations().isEmpty()){
			entry.setAnnotations(this.annotationService.findAnnotations(entry.getUniqueName()));
		}
		if(entry.getIsoforms() == null || entry.getIsoforms().isEmpty()){
			entry.setIsoforms(this.isoformService.findIsoformsByEntryName(entry.getUniqueName()));
		}
		if(entry.getPublications() == null || entry.getPublications().isEmpty()){
			entry.setPublications(this.publicationService.findPublicationsByMasterUniqueName(entry.getUniqueName()));
		}
		if(entry.getXrefs() == null || entry.getXrefs().isEmpty()){
			entry.setXrefs(this.xrefService.findDbXrefsByMaster(entry.getUniqueName()));
		}
		if(entry.getExperimentalContexts() == null || entry.getExperimentalContexts().isEmpty()){
			entry.setExperimentalContexts(this.experimentalContextService.findExperimentalContextsByEntryName(entry.getUniqueName()));
		}
	}
	


}
