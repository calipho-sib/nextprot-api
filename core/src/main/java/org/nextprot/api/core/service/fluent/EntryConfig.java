package org.nextprot.api.core.service.fluent;

import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.service.export.format.EntryBlocks;

public class EntryConfig {
	
	private boolean overview, publications, genomicMappings, xrefs, keywords, identifiers, chromosomalLocations, interactions, targetIsoforms, generalAnnotations, antibodyMappings, experimentalContext;
	private boolean enzymes;
	private boolean withoutAdditionalReferences = false; // by default we put xrefs, publications, experimental contexts
	private boolean withoutProperties = false; //by default we get properties
	
	private EntryConfig(String entryName){
		if(entryName.toUpperCase().startsWith("NX_")){
			this.entryName = entryName;
		}else {
			this.entryName = "NX_" + entryName;
		}
	}
	
	public static EntryConfig newConfig(String entryName){
		return new EntryConfig(entryName);
	}

	public boolean hasOverview() {
		return overview;
	}
	
	public boolean hasNoAdditionalReferences() {
		return withoutAdditionalReferences;
	}
	
	public boolean hasNoProperties() {
		return withoutProperties;
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

	public boolean hasExperimentalContext() {
		return experimentalContext;
	}

	public EntryConfig withOverview() {
		this.overview = true; return this;
	}

	public EntryConfig withPublications() {
		this.publications = true; return this;
	}

	public EntryConfig withGenomicMappings() {
		this.genomicMappings = true; return this;
	}

	public EntryConfig withXrefs() {
		this.xrefs = true; return this;
	}

	public EntryConfig withKeywords() {
		this.keywords = true; return this;
	}

	public EntryConfig withIdentifiers() {
		this.identifiers = true; return this;
	}

	public EntryConfig withChromosomalLocations() {
		this.chromosomalLocations = true; return this;
	}

	public EntryConfig withInteractions() {
		this.interactions = true; return this;
	}

	public EntryConfig withTargetIsoforms() {
		this.targetIsoforms = true; return this;
	}

	public EntryConfig withAnnotations() {
		this.generalAnnotations = true; return this;
	}

	public EntryConfig withAntibodyMappings() {
		this.antibodyMappings = true; return this;
	}

	public EntryConfig withExperimentalContexts() {
		this.experimentalContext = true; return this;
	}

	public EntryConfig withoutProperties() {
		this.withoutProperties = true; return this; 
	}
	
	public EntryConfig withEnzymes() {
		this.enzymes = true; return this; //TODO is this necessary? can't we write a method on top of overview names???
	}


	public EntryConfig withEverything() {
		this.withOverview().withAnnotations().withPublications().withXrefs().withKeywords()
		.withIdentifiers().withChromosomalLocations().withGenomicMappings().withInteractions()
		.withTargetIsoforms().withAntibodyMappings()
		.withExperimentalContexts().withEnzymes();
		return this;
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
	public EntryConfig with(String blockOrSubpart) {

		if("entry".equals(blockOrSubpart.toLowerCase())){
			this.withEverything();
		}else  if(EntryBlocks.containsBlock(blockOrSubpart.toUpperCase())){
			this.withEntryBlock(EntryBlocks.valueOfViewName(blockOrSubpart.toUpperCase()));
		}else {
			try{
				this.subpart = AnnotationApiModel.getDecamelizedAnnotationTypeName(blockOrSubpart);
			} catch (IllegalArgumentException ec) {
				throw new NextProtException("Block or subpart " + blockOrSubpart + " not found. Please look into...");
			}
		}
		return this;
		
	}


	public EntryConfig withoutAdditionalReferences() {
		this.withoutAdditionalReferences = true;
		return this;
	}
	
	//Overload with NPViews
	private EntryConfig withEntryBlock(EntryBlocks block) {

		switch (block) {
			case FULL_ENTRY: this.withEverything(); break;
			case ACCESSION: break;//TODO withProperties break;
			case OVERVIEW: this.withOverview(); break;
			case PUBLICATION: this.withPublications(); break;
			case XREF: this.withXrefs(); break;
			case IDENTIFIER: this.withIdentifiers(); break;
			case CHROMOSOMAL_LOCATION: this.withChromosomalLocations(); break;
			case GENOMIC_MAPPING: this.withGenomicMappings(); break;
			case ISOFORM: this.withTargetIsoforms(); break;
			case ANNOTATION: this.withAnnotations(); break;
			case ANTIBODY:  this.withAntibodyMappings(); break;
			//case PEPTIDE_MAPPING: this.withPeptideMappings(); break;
			//case SRM_PEPTIDE_MAPPING:  this.withSrmPeptideMappings(); break;
			case EXPERIMENTAL_CONTEXT: this.withExperimentalContexts(); break;
			default: {throw new NextProtException(block + " block not found");}
		}
		return this;

	}


	public boolean hasSubPart() {
		return (this.subpart != null);
	}

	public String getEntryName() {
		return this.entryName;
	}

	public boolean hasEnzymes() {
		return this.enzymes;
	}

	public boolean hasSubPart(AnnotationApiModel subpart) {
		return subpart.equals(this.subpart);
	}



}
