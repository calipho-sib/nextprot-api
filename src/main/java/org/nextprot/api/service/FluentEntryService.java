package org.nextprot.api.service;

import org.nextprot.api.domain.Entry;
import org.nextprot.api.domain.export.ExportTXTTemplate;
import org.nextprot.api.domain.export.ExportTemplate;
import org.nextprot.api.domain.export.ExportXMLTemplate;
import org.nextprot.core.exception.NextProtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

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

	public FluentEntry getNewEntry(String entryName) {
		return new FluentEntry(entryName);
	}

	public class FluentEntry {
		private Entry entry = null;
		private String entryName;

		public FluentEntry(String entryName) {
			this.entryName = entryName;
			this.entry = new Entry(entryName);
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
			entry.setPeptideMappings(peptideMappingService.findPeptideMappingByMasterId(masterId));
			return this;
		}

		public FluentEntry withEverything() {
			return this.withOverview().withGeneralAnnotations().withPublications().withXrefs().withKeywords().withIdentifiers().withChromosomalLocations().withGenomicMappings().withInteractions()
					.withTargetIsoforms().withAntibodyMappings().withPeptideMappings();
		}

		public Entry getEntry() {
			return entry;
		}

		public Entry withTemplate(ExportTemplate _template) {

			if (ExportXMLTemplate.class.isInstance(_template)) {

				ExportXMLTemplate template = (ExportXMLTemplate) _template;

				switch (template) {
				case FULL:
					return this.withEverything().getEntry();
				case ACCESSIONS_ONLY:
					return this.getEntry();
				case OVERVIEW:
					return this.withOverview().getEntry();
				case PUBLICATIONS:
					return this.withPublications().getEntry();
				case XREFS:
					return this.withXrefs().getEntry();
				case KEYWORDS:
					return this.withKeywords().getEntry();
				case IDENTIFIERS:
					return this.withIdentifiers().getEntry();
				case CHROMOSOMAL_LOCATIONS:
					return this.withChromosomalLocations().getEntry();
				case GENOMIC_MAPPINGS:
					return this.withGenomicMappings().getEntry();
				case INTERACTIONS:
					return this.withInteractions().getEntry();
				case PROTEIN_SEQUENCE:
					return this.withTargetIsoforms().getEntry();
				case GENERAL_ANNOTATIONS:
					return this.withGeneralAnnotations().getEntry();
				case ANTIBODY_MAPPINGS:
					return this.withAntibodyMappings().getEntry();
				case PEPTIDE_MAPPINGS:
					return this.withPeptideMappings().getEntry();

				default:
					throw new NextProtException(template + " export xml template case not found");

				}

			} else if (ExportTXTTemplate.class.isInstance(_template)) {

				ExportTXTTemplate template = (ExportTXTTemplate) _template;
				switch (template) {
				case ACCESSIONS_ONLY:
					return this.getEntry();
				default:
					throw new NextProtException(template + " export txt template case not found");
				}

			}

			throw new NextProtException(_template + " export template case not found");

		}
	}

}
