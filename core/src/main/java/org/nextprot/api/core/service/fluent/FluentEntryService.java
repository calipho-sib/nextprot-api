package org.nextprot.api.core.service.fluent;

import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.EntryUtils;
import org.nextprot.api.core.service.AnnotationService;
import org.nextprot.api.core.service.AntibodyMappingService;
import org.nextprot.api.core.service.DbXrefService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class FluentEntryService {

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

	public Entry build(EntryConfig fluentEntry) {
	
		String entryName = fluentEntry.getEntryName();
		Entry entry = new Entry(entryName);

//		synchronized (){

			if(fluentEntry.hasOverview()){
				entry.setOverview(this.overviewService.findOverviewByEntry(entryName));
			}
			if(fluentEntry.hasPublications()){
				entry.setPublications(this.publicationService.findPublicationsByMasterUniqueName(entryName));
			}
			if(fluentEntry.hasXrefs()){
				entry.setXrefs(this.xrefService.findDbXrefsByMaster(entryName));
			}
			if(fluentEntry.hasIdentifiers()){
				entry.setIdentifiers(this.identifierService.findIdentifiersByMaster(entryName));
			}
			if(fluentEntry.hasChromosomalLocations()){
				entry.setChromosomalLocations(this.geneService.findChromosomalLocationsByEntry(entryName));
			}
			if(fluentEntry.hasGenomicMappings()){
				entry.setGenomicMappings(this.genomicMappingService.findGenomicMappingsByEntryName(entryName));
			}
			if(fluentEntry.hasTargetIsoforms()){
				entry.setIsoforms(this.isoformService.findIsoformsByEntryName(entryName));
			}
			if(fluentEntry.hasGeneralAnnotations()){
				entry.setAnnotations(this.annotationService.findAnnotations(entryName));
			}
			if(fluentEntry.hasAntibodyMappings()){
				entry.setAntibodyMappings(this.antibodyMappingService.findAntibodyMappingByUniqueName(entryName));
			}
			if(fluentEntry.hasPeptideMappings()){
				entry.setPeptideMappings(this.peptideMappingService.findNaturalPeptideMappingByMasterUniqueName(entryName));
			}
			if(fluentEntry.hasSrmPeptideMappings()){
				entry.setSrmPeptideMappings(this.peptideMappingService.findSyntheticPeptideMappingByMasterUniqueName(entryName));
			}
			if(fluentEntry.hasExperimentalContext()){
				entry.setExperimentalContexts(this.experimentalContextService.findExperimentalContextsByEntryName(entryName));
			}
			
			
			if(fluentEntry.hasGeneralAnnotations() || fluentEntry.hasSubPart()
					   || fluentEntry.hasAntibodyMappings() || fluentEntry.hasPeptideMappings() || fluentEntry.hasSrmPeptideMappings()){ //TODO should be added in annotation list
						setEntryAdditionalInformation(entry); //adds isoforms, publications, xrefs and experimental contexts
		} 

//		}
		
		//CPU Intensive
		if(fluentEntry.hasGeneralAnnotations() || fluentEntry.hasSubPart()
		   || fluentEntry.hasAntibodyMappings() || fluentEntry.hasPeptideMappings() || fluentEntry.hasSrmPeptideMappings()){ //TODO should be added in annotation list
			
			if(fluentEntry.hasSubPart()){
				return EntryUtils.filterEntryBySubPart(entry, fluentEntry.getSubpart());
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
