package org.nextprot.api.core.service.fluent;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.KeyValueRepresentation;
import org.nextprot.api.core.service.export.format.EntryBlock;

import java.util.ArrayList;
import java.util.List;

public class EntryConfig implements KeyValueRepresentation{
	
	private boolean overview, publications, genomicMappings, xrefs, identifiers, chromosomalLocations, interactions, targetIsoforms, generalAnnotations, experimentalContext, mdata;
	private boolean enzymes;
	private boolean goldOnly = false;
	private boolean bed = true;
	private boolean withoutAdditionalReferences = false; // by default we put xrefs, publications, experimental contexts,mdata
	private boolean withoutProperties = false; //by default we get properties
	private AnnotationCategory subpart;
	private List<AnnotationCategory> subparts = new ArrayList<>();
	private final String entryName;

	private EntryConfig(String entryName) {
		if(entryName.toUpperCase().startsWith("NX_")){
			this.entryName = entryName;
		} else {
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

	public boolean hasExperimentalContext() {
		return experimentalContext;
	}

	public boolean hasMdata() {
		return mdata;
	}

	public boolean hasSubPart() {
		return (this.subpart != null);
	}

	public boolean hasSubParts() {
		return this.subparts.size() > 0;
	}

	public boolean hasGoldOnly() {
		return this.goldOnly;
	}

	public boolean hasBed() {
		return this.bed;
	}

	public String getEntryName() {
		return this.entryName;
	}

	public boolean hasEnzymes() {
		return this.enzymes;
	}

	public AnnotationCategory getSubpart() {
		return subpart;
	}

	public List<AnnotationCategory> getSubparts() {
		return subparts;
	}

	public EntryConfig withOverview() {
		this.overview = true; return this;
	}
	
	public EntryConfig withGoldOnly(Boolean goldOnly) {
		if(goldOnly != null){
			this.goldOnly = goldOnly;
		}
		return this;
	}

	public EntryConfig withBed(Boolean bed) {
		if(bed != null){
			this.bed = bed;
		}
		return this;
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

	public EntryConfig withExperimentalContexts() {
		this.experimentalContext = true; return this;
	}

	public EntryConfig withMdata() {
		this.mdata = true; return this;
	}

	public EntryConfig withoutProperties() {
		this.withoutProperties = true; return this; 
	}

	public EntryConfig withoutAdditionalReferences() {
		this.withoutAdditionalReferences = true;
		return this;
	}

	public EntryConfig withEnzymes() {
		this.enzymes = true; return this; //TODO is this necessary? can't we write a method on top of overview names???
	}

	public EntryConfig withEverything() {
		this.withOverview().withAnnotations().withPublications().withXrefs()
		.withIdentifiers().withChromosomalLocations().withGenomicMappings().withInteractions()
		.withTargetIsoforms().withExperimentalContexts().withMdata().withEnzymes();
		return this;
	}

	/**
	 * Could be a block or subpart
	 * @param blockOrSubpart
	 * @return
	 */
	public EntryConfig with(String blockOrSubpart) {

		if("entry".equals(blockOrSubpart.toLowerCase())){
			withEverything();
		}
		else if(EntryBlock.containsBlock(blockOrSubpart.toUpperCase())){
			withBlock(EntryBlock.valueOfViewName(blockOrSubpart.toUpperCase()));
		}
		else {
			try{
				subpart = AnnotationCategory.getDecamelizedAnnotationTypeName(blockOrSubpart);
			} catch (IllegalArgumentException ec) {
				throw new NextProtException("Block or subpart " + blockOrSubpart + " not found. Please look into...");
			}
		}
		return this;
	}

	public EntryConfig withSubParts(List<String> subparts) {
		for(String category: subparts) {
			try {
				this.subparts.add(AnnotationCategory.getDecamelizedAnnotationTypeName(category));
			} catch (IllegalArgumentException ec) {
				throw new NextProtException("Block or subpart " + category + " not found. Please look into...");
			}
		}
		return this;
	}

	//Overload with NPViews
	public EntryConfig withBlock(EntryBlock block) {

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
			case EXPERIMENTAL_CONTEXT: this.withExperimentalContexts(); break;
			case MDATA: this.withMdata(); break;
			default: {throw new NextProtException(block + " block not found");}
		}
		return this;
	}

	@Override
	public String toKeyValueString() {
		StringBuilder sb = new StringBuilder();
		if(this.hasSubPart()){
			sb.append("subpart=" + subpart + ";");
		}
		if(generalAnnotations){
			sb.append("annotations=true");
		}
		if(xrefs){
			sb.append("xrefs=true");
		}
		if(experimentalContext){
			sb.append("experimentalContexts=true");
		}
		if(mdata){
			sb.append("mdata=true");
		}

		return sb.toString();
	}
}
