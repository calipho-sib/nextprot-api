package org.nextprot.api.core.service.fluent;

import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.ExperimentalContext;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.*;
import org.nextprot.api.core.service.export.format.NPViews;
import org.nextprot.api.core.utils.AnnotationUtils;
import org.nextprot.api.core.utils.ExperimentalContextUtil;
import org.nextprot.api.core.utils.PublicationUtils;
import org.nextprot.api.core.utils.XrefUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Lazy
@Service
public class FluentEntryService {

	@Autowired
	private OverviewService overviewService;
	@Autowired
	private PublicationService publicationService;
	@Autowired
	private DbXrefService xrefService;
	@Autowired
	private KeywordService kwService;
	@Autowired
	private IdentifierService identifierService;
	@Autowired
	private GeneService geneService;
	@Autowired
	private GenomicMappingService genomicMappingService;
	@Autowired
	private IsoformService isoformService;
	@Autowired
	private MasterIdentifierService masterIdentifierService;
	@Autowired
	private AnnotationService annotationService;
	@Autowired
	private PeptideMappingService peptideMappingService;
	@Autowired
	private AntibodyMappingService antibodyMappingService;
	@Autowired
	private InteractionService interactionService;
	@Autowired
	private ExperimentalContextService ecService;
	
	@Autowired
	private EntryPropertiesService entryPropertiesService;
	
	public FluentEntry newFluentEntry(String entryName) {
		return new FluentEntry(entryName);
	}

	public class FluentEntry {
		private Entry entry = null;
		private String entryName;

		public FluentEntry(String entryName) {
			this.entryName = entryName;
			this.entry = new Entry(entryName);
			this.entry.setProperties(entryPropertiesService.findEntryProperties(entryName));

		}

		public FluentEntry withOverview() {
			entry.setOverview(overviewService.findOverviewByEntry(entryName));
			return this;
		}

		public FluentEntry withPublications() {
			entry.setPublications(publicationService.findPublicationsByMasterUniqueName(entryName));
			return this;
		}

		public FluentEntry withGenomicMappings() {
			entry.setGenomicMappings(genomicMappingService.findGenomicMappingsByEntryName(entryName));
			return this;
		}

		public FluentEntry withXrefs() {
			entry.setXrefs(xrefService.findDbXrefsByMaster(entryName));
			return this;
		}

		public FluentEntry withKeywords() {
			entry.setKeywords(kwService.findKeywordByMaster(entryName));
			return this;
		}

		public FluentEntry withIdentifiers() {
			entry.setIdentifiers(identifierService.findIdentifiersByMaster(entryName));
			return this;
		}

		public FluentEntry withChromosomalLocations() {
			entry.setChromosomalLocations(geneService.findChromosomalLocationsByEntry(entryName));
			return this;
		}

		public FluentEntry withInteractions() {
			entry.setInteractions(interactionService.findInteractionsByEntry(entryName));
			return this;
		}

		public FluentEntry withTargetIsoforms() {
			entry.setIsoforms(isoformService.findIsoformsByEntryName(entryName));
			return this;
		}

		public FluentEntry withGeneralAnnotations() {
			entry.setAnnotations(annotationService.findAnnotations(entryName));
			return this;
		}

		public FluentEntry withAntibodyMappings() {
			Long masterId = masterIdentifierService.findIdByUniqueName(entryName);
			entry.setAntibodyMappings(antibodyMappingService.findAntibodyMappingByMasterId(masterId));
			return this;
		}

		public FluentEntry withPeptideMappings() {
			Long masterId = masterIdentifierService.findIdByUniqueName(entryName);
			entry.setPeptideMappings(peptideMappingService.findNaturalPeptideMappingByMasterId(masterId));
			return this;
		}

		public FluentEntry withSrmPeptideMappings() {
			Long masterId = masterIdentifierService.findIdByUniqueName(entryName);
			entry.setSrmPeptideMappings(peptideMappingService.findSyntheticPeptideMappingByMasterId(masterId));
			return this;
		}

		public FluentEntry withExperimentalContexts() {
			entry.setExperimentalContexts(ecService.findExperimentalContextsByEntryName(entryName));
			return this;
		}

		public FluentEntry withEverything() {
			this.withOverview().withGeneralAnnotations().withPublications().withXrefs().withKeywords()
			.withIdentifiers().withChromosomalLocations().withGenomicMappings().withInteractions()
			.withTargetIsoforms().withAntibodyMappings().withPeptideMappings().withSrmPeptideMappings()
			.withExperimentalContexts();
			
			return this;
		}

		public Entry build() {
			return entry;
		}

		public Entry buildWithView(String view) {
			if(view.equals("entry")){
				return this.withEverything().build();
			}
			try {
				NPViews npView = NPViews.valueOfViewName(view);
				return buildEntrySubPart(npView);
			} catch (IllegalArgumentException ev) {
				try {
					AnnotationApiModel annotationCategory = AnnotationApiModel.getDecamelizedAnnotationTypeName(view);
					return getEntryFiltered(annotationCategory);
				} catch (IllegalArgumentException ec) {
					throw new NextProtException("View " + view + " not found. Please look into...");
				}
			}
		}

		private Entry buildEntrySubPart(NPViews npView) {

			switch (npView) {
			case FULL_ENTRY:
				return this.withEverything().build();
			case ACCESSION:
				return this.build();
			case OVERVIEW:
				return this.withOverview().build();
			case PUBLICATION:
				return this.withPublications().build();
			case XREF:
				return this.withXrefs().build();
			case IDENTIFIER:
				return this.withIdentifiers().build();
			case CHROMOSOMAL_LOCATION:
				return this.withChromosomalLocations().build();
			case GENOMIC_MAPPING:
				return this.withGenomicMappings().build();
			/*
			case INTERACTION:
				return this.withInteractions().build();  // now treated as annotation subpart
			*/
			case ISOFORM:
				return this.withTargetIsoforms().build();
			case ANNOTATION:
				return this.withGeneralAnnotations().build();
			case ANTIBODY:
				return this.withAntibodyMappings().build();
			case PEPTIDE:
				return this.withPeptideMappings().build();
			case SRM_PEPTIDE:
				return this.withSrmPeptideMappings().build();
			case EXPERIMENTAL_CONTEXT:
				return this.withExperimentalContexts().build();

			default: {

				throw new NextProtException(npView + " export xml template case not found");

			}

			}

		}

		public Entry getEntryFiltered(AnnotationApiModel annotationCategory) {

			List<Annotation> annotations = annotationService.findAnnotations(entryName);
			List<DbXref> xrefs = xrefService.findDbXrefsByMaster(entryName);
			List<Publication> publications = publicationService.findPublicationsByMasterUniqueName(entryName);
			List<ExperimentalContext> ecs = ecService.findExperimentalContextsByEntryName(entryName);
			// Filter if necessary
			if (annotationCategory != null) {
				annotations = AnnotationUtils.filterAnnotationsByCategory(annotations, annotationCategory);
				Set<Long> xrefIds = AnnotationUtils.getXrefIdsForAnnotations(annotations);
				xrefIds.addAll(AnnotationUtils.getXrefIdsForInteractionsInteractants(annotations));
				xrefs = XrefUtils.filterXrefsByIds(xrefs, xrefIds);
				publications = PublicationUtils.filterPublicationsByIds(publications, AnnotationUtils.getPublicationIdsForAnnotations(annotations));
				ecs = ExperimentalContextUtil.filterExperimentalContextsByIds(ecs, AnnotationUtils.getExperimentalContextIdsForAnnotations(annotations));
				entry.setIsoforms(isoformService.findIsoformsByEntryName(entryName));
			}
			entry.setAnnotations(annotations);
			entry.setXrefs(xrefs);
			entry.setPublications(publications);
			entry.setExperimentalContexts(ecs);
			return entry;
		}

	}
}
