package org.nextprot.api.core.service.fluent;

import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.export.format.EntryBlocks;

public class EntryBuilder {
	
	private FluentEntryService fluentEntryService;
	private boolean overview, publications, genomicMappings, xrefs, keywords, identifiers, chromosomalLocations, interactions, targetIsoforms, generalAnnotations, antibodyMappings, peptideMappings, srmPeptideMappings, experimentalContext;

	public EntryBuilder(String entryName, FluentEntryService fluentEntryService){
		this.entryName = entryName;
		this.fluentEntryService = fluentEntryService;
	}

	public boolean hasOverview() {
		return overview;
	}

	public boolean hasPublications() {
		return publications;
	}

	public boolean hasGenomicMappings() {
		return genomicMappings;
	}

	public boolean hasXrefs() {
		return xrefs;
	}

	public boolean hasKeywords() {
		return keywords;
	}

	public boolean hasIdentifiers() {
		return identifiers;
	}

	public boolean hasChromosomalLocations() {
		return chromosomalLocations;
	}

	public boolean hasInteractions() {
		return interactions;
	}

	public boolean hasTargetIsoforms() {
		return targetIsoforms;
	}

	public boolean hasGeneralAnnotations() {
		return generalAnnotations;
	}

	public boolean hasAntibodyMappings() {
		return antibodyMappings;
	}

	public boolean hasPeptideMappings() {
		return peptideMappings;
	}

	public boolean hasSrmPeptideMappings() {
		return srmPeptideMappings;
	}

	public boolean hasExperimentalContext() {
		return experimentalContext;
	}

	public EntryBuilder withOverview() {
		this.overview = true; return this;
	}

	public EntryBuilder withPublications() {
		this.publications = true; return this;
	}

	public EntryBuilder withGenomicMappings() {
		this.genomicMappings = true; return this;
	}

	public EntryBuilder withXrefs() {
		this.xrefs = true; return this;
	}

	public EntryBuilder withKeywords() {
		this.keywords = true; return this;
	}

	public EntryBuilder withIdentifiers() {
		this.identifiers = true; return this;
	}

	public EntryBuilder withChromosomalLocations() {
		this.chromosomalLocations = true; return this;
	}

	public EntryBuilder withInteractions() {
		this.interactions = true; return this;
	}

	public EntryBuilder withTargetIsoforms() {
		this.targetIsoforms = true; return this;
	}

	public EntryBuilder withGeneralAnnotations() {
		this.generalAnnotations = true; return this;
	}

	public EntryBuilder withAntibodyMappings() {
		this.antibodyMappings = true; return this;
	}

	public EntryBuilder withPeptideMappings() {
		this.peptideMappings = true; return this;
	}

	public EntryBuilder withSrmPeptideMappings() {
		this.srmPeptideMappings = true; return this;
	}

	public EntryBuilder withExperimentalContexts() {
		this.experimentalContext = true; return this;
	}


	public EntryBuilder withEverything() {
		this.withOverview().withGeneralAnnotations().withPublications().withXrefs().withKeywords()
		.withIdentifiers().withChromosomalLocations().withGenomicMappings().withInteractions()
		.withTargetIsoforms().withAntibodyMappings().withPeptideMappings().withSrmPeptideMappings()
		.withExperimentalContexts();
		return this;
	}


	public Entry build() {
		//TODO ahhhrrrrg this looks like a cycle dependency 
		return this.fluentEntryService.build(this);
	}

	private AnnotationApiModel subpart;
	private String entryName;
	public AnnotationApiModel getSubpart() {
		return subpart;
	}

	/**
	 * could be a block or supart
	 * @param blockOrSubpart
	 * @return
	 */
	public EntryBuilder with(String blockOrSubpart) {

		if("entry".equals(blockOrSubpart.toLowerCase())){
			this.withEverything();
		}
		
		if(EntryBlocks.containsBlock(blockOrSubpart)){
			this.withEntryBlock(EntryBlocks.valueOfViewName(blockOrSubpart));
		}else {
			try{
				this.subpart = AnnotationApiModel.getDecamelizedAnnotationTypeName(blockOrSubpart);
			} catch (IllegalArgumentException ec) {
				throw new NextProtException("Block or subpart " + blockOrSubpart + " not found. Please look into...");
			}
		}
		return this;
		
	}
	
	
	//Overload with NPViews
	private EntryBuilder withEntryBlock(EntryBlocks npView) {

		switch (npView) {
			case FULL_ENTRY: this.withEverything(); break;
			case ACCESSION: break;//TODO withProperties break;
			case OVERVIEW: this.withOverview(); break;
			case PUBLICATION: this.withPublications(); break;
			case XREF: this.withXrefs(); break;
			case IDENTIFIER: this.withIdentifiers(); break;
			case CHROMOSOMAL_LOCATION: this.withChromosomalLocations(); break;
			case GENOMIC_MAPPING: this.withGenomicMappings(); break;
			case ISOFORM: this.withTargetIsoforms(); break;
			case ANNOTATION: this.withGeneralAnnotations(); break;
			case ANTIBODY:  this.withAntibodyMappings(); break;
			case PEPTIDE: this.withPeptideMappings(); break;
			case SRM_PEPTIDE:  this.withSrmPeptideMappings(); break;
			case EXPERIMENTAL_CONTEXT: this.withExperimentalContexts(); break;
			default: {throw new NextProtException(npView + " export xml template case not found");}
		}
		return this;

	}


	public boolean hasSubPart() {
		return (this.subpart != null);
	}

	public String getEntryName() {
		return this.entryName;
	}

}
