package org.nextprot.api.core.service.impl;

import com.google.common.collect.ImmutableList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.*;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.utils.EntryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
class EntryBuilderServiceImpl implements EntryBuilderService, InitializingBean{

	@Autowired private OverviewService overviewService;
	@Autowired private PublicationService publicationService;
	@Autowired private DbXrefService xrefService;
	@Autowired private IdentifierService identifierService;
	@Autowired private GeneService geneService;
	@Autowired private GenomicMappingService genomicMappingService;
	@Autowired private IsoformService isoformService;
	@Autowired private MasterIdentifierService masterIdentifierService;
	@Autowired private AnnotationService annotationService;
	@Autowired private InteractionService interactionService;
	@Autowired private ExperimentalContextService expCtxService;
	@Autowired private MdataService mdataService;
	@Autowired private TerminologyService terminologyService; //TODO shouldn't we have method in entry to get the enzymes based on the EC names???
	@Autowired private EntryPropertiesService entryPropertiesService;
	@Autowired private GnomadXrefService gnomadXrefService;

	private static Map<String, Object> objectLocks = new ConcurrentHashMap<>();

	private static final Log LOGGER = LogFactory.getLog(EntryBuilderServiceImpl.class);


	@Override
	public Entry build(EntryConfig entryConfig) {
	
		String entryName = EntryUtils.getEntryName(entryConfig.getEntryName());
		Entry entry = new Entry(entryName);

		//Lock per entry in case the cache is not set yet (should be quite) fast thougth
		synchronized (getOrPutSynchronizer(entryName)){

			//Always set properties about the entry, unless it was explicitly said not to set them
			if(!entryConfig.hasNoProperties()){
				entry.setProperties(entryPropertiesService.findEntryProperties(entryName));
			}
		
			if(entryConfig.hasOverview()){
				entry.setOverview(this.overviewService.findOverviewByEntry(entryName));
			}
			if(entryConfig.hasPublications()){
				entry.setPublications(this.publicationService.findPublicationsByEntryName(entryName));
			}
			if(entryConfig.hasXrefs()){
				this.setXrefs(entry, entryName);
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
				if (entryConfig.hasBed()) {
					entry.setAnnotations(
						this.annotationService.findAnnotations(entryName));
				} else  {
					entry.setAnnotations(
						this.annotationService.findAnnotationsExcludingBed(entryName));
				}
			}
			
			if(entryConfig.hasMdata()){
				List<Annotation> annotations = entry.getAnnotations();
				//In case we did't set annotations but we need them to find experimental contexts
				if(annotations == null) {
					annotations = this.annotationService.findAnnotations(entryName);
				}
				List<Long> mdataIds = new ArrayList<>(EntryUtils.getMdataIds(annotations));
				entry.setMdataList(mdataService.findMdataByIds(mdataIds));
			}

			if(entryConfig.hasExperimentalContext()){
				List<Annotation> annotations = entry.getAnnotations();
				//In case we did't set annotations but we need them to find experimental contexts
				if(annotations == null) {
					annotations = this.annotationService.findAnnotations(entryName);
				}
				Set<Long> ecIds = EntryUtils.getExperimentalContextIds(annotations);
				entry.setExperimentalContexts(expCtxService.findExperimentalContextsByIds(ecIds));
			}

			if(entryConfig.hasInteractions()){
				entry.setInteractions(this.interactionService.findInteractionsByEntry(entryName));
			}
			if(entryConfig.hasEnzymes()){
				entry.setEnzymes(terminologyService.findEnzymeByMaster(entryName));
			}
			
			if((entryConfig.hasGeneralAnnotations() || entryConfig.hasSubPart())){ //TODO should be added in annotation list
				setEntryAdditionalInformation(entry, entryConfig); //adds isoforms, publications, xrefs and experimental contexts
			}
		}
		//CPU Intensive
		if(entryConfig.hasSubPart() || entryConfig.hasGoldOnly()){ //TODO should be added in annotation list
			return EntryUtils.filterEntryBySubPart(entry, entryConfig);
		} else {
			return entry;
		}

	}
	
	private static Object getOrPutSynchronizer(String entryName) {
		if(objectLocks.containsKey(entryName)){
			return objectLocks.get(entryName);
		}else {
			Object o = new Object();
			objectLocks.put(entryName, o);
			return o;
		}
	}

	private void setEntryAdditionalInformation(Entry entry, EntryConfig config){

		if(entry.getAnnotations() == null || entry.getAnnotations().isEmpty()){
			if (config.hasBed()) {
				entry.setAnnotations(
					this.annotationService.findAnnotations(entry.getUniqueName()));
			} else  {
				entry.setAnnotations(
					this.annotationService.findAnnotationsExcludingBed(entry.getUniqueName()));
			}
		}
		
		if(!config.hasNoAdditionalReferences()){

			if(entry.getIsoforms() == null || entry.getIsoforms().isEmpty()){
				entry.setIsoforms(this.isoformService.findIsoformsByEntryName(entry.getUniqueName()));
			}
			if(entry.getPublications() == null || entry.getPublications().isEmpty()){
				entry.setPublications(this.publicationService.findPublicationsByEntryName(entry.getUniqueName()));
			}
			if(entry.getXrefs() == null || entry.getXrefs().isEmpty()){
				setXrefs(entry, entry.getUniqueName());
			}
			if(entry.getExperimentalContexts() == null || entry.getExperimentalContexts().isEmpty()){
				Set<Long> ecIds = EntryUtils.getExperimentalContextIds(entry.getAnnotations());
				entry.setExperimentalContexts(expCtxService.findExperimentalContextsByIds(ecIds));
			}
			if(entry.getMdataList() == null || entry.getMdataList().isEmpty()){
				List<Long> mdIds = new ArrayList<>(EntryUtils.getMdataIds(entry.getAnnotations()));
				entry.setMdataList(mdataService.findMdataByIds(mdIds));
			}

		}
	}

	private void setXrefs(Entry entry, String entryName) {
		// Generates the dbxrefs
		List<DbXref> dbXrefs = this.xrefService.findDbXrefsByMaster(entryName);

		// Generates the gnomad xrefs
		List<DbXref> gnomadXrefs = this.gnomadXrefService.findGnomadDbXrefsByMaster(entryName);

		List<DbXref> allXrefs = new ImmutableList.Builder<DbXref>()
										.addAll(dbXrefs)
										.addAll(gnomadXrefs)
										.build();
		entry.setXrefs(allXrefs);
	}

	@Override
	public Entry buildWithEverything(String entryName) {
		return this.build(EntryConfig.newConfig(entryName).withEverything());
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		for(String uniqueName : masterIdentifierService.findUniqueNames()){
			objectLocks.put(uniqueName, new Object());
		}
	}

}
