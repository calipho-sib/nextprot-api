package org.nextprot.api.core.domain;

import java.util.List;
import java.util.Set;

import org.nextprot.api.core.domain.annotation.Annotation;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class Entry {

	private String uniqueName;

	private Overview overview;

	private List<Publication> publications;

	private List<DbXref> xrefs;

	private List<Keyword> keywords;

	private List<Identifier> identifiers;

	private List<ChromosomalLocation> chromosomalLocations;

	private List<Isoform> isoforms;

	private List<Annotation> annotations;

	private List<PeptideMapping> peptideMappings;

	private List<AntibodyMapping> antibodyMappings;

	private List<GenomicMapping> genomicMappings;

	private List<Interaction> interactions;

	private List<Terminology> enzymes;
	
	public Entry(String uniqueName) {
		this.uniqueName = uniqueName;
	}

	public String getUniqueName() {
		return uniqueName;
	}
	
	public String getUniprotName() {
	  return uniqueName.substring(3);
	}

	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
	}

	public Overview getOverview() {
		return overview;
	}

	public void setOverview(Overview overview) {
		this.overview = overview;
	}

	public List<Publication> getPublications() {
		return publications;
	}

	public void setPublications(List<Publication> publications) {
		this.publications = publications;
	}

	public List<DbXref> getXrefs() {
		return xrefs;
	}

	public void setXrefs(List<DbXref> xrefs) {
		this.xrefs = xrefs;
	}

	public List<Keyword> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<Keyword> keywords) {
		this.keywords = keywords;
	}

	public List<Identifier> getIdentifiers() {
		return identifiers;
	}

	public void setIdentifiers(List<Identifier> identifiers) {
		this.identifiers = identifiers;
	}

	public List<ChromosomalLocation> getChromosomalLocations() {
		return chromosomalLocations;
	}

	public void setChromosomalLocations(List<ChromosomalLocation> chromosomalLocations) {
		this.chromosomalLocations = chromosomalLocations;
	}

	public List<Isoform> getIsoforms() {
		return isoforms;
	}

	public void setIsoforms(List<Isoform> isoforms) {
		this.isoforms = isoforms;
	}

	public List<Annotation> getAnnotations() {
		return annotations;
	}

	public List<AntibodyMapping> getAntibodiesByIsoform(String isoform) {
		List<AntibodyMapping> abmlist = Lists.newArrayList();
		for (AntibodyMapping abm: this.antibodyMappings) {
			if (abm.isSpecificForIsoform(isoform)) abmlist.add(abm);
		}
		return abmlist;
	}
	
	public List<PeptideMapping> getPeptidesByIsoform(String isoform) {
		List<PeptideMapping> ppmlist = Lists.newArrayList();
		for (PeptideMapping ppm: this.peptideMappings) {
			if (ppm.isSpecificForIsoform(isoform)) ppmlist.add(ppm);
		}
		return ppmlist;
	}
	
	public List<Annotation> getAnnotationsByIsoform(String isoform) {
		List<Annotation> filteredAnnotations = Lists.newArrayList();
		for (Annotation a : annotations) {
			//Should not be enough to determine if the annotation is on the isoform or not
			if(a.isAnnotationValidForIsoform(isoform)){
					filteredAnnotations.add(a);
			}
		}
		return filteredAnnotations;
	}

	public List<Interaction> getInteractionsByIsoform(String isoform) {
		List<Interaction> filteredInteractions = Lists.newArrayList();
		for (Interaction a : interactions) {
			if(a.isInteractionValidForIsoform(isoform)){
					filteredInteractions.add(a);
			}
		}
		return filteredInteractions;
	}
	
	
	public List<Annotation> getAnnotationsByCategory(String category) {
		List<Annotation> filteredAnnotations = Lists.newArrayList();
		for (Annotation a : annotations) {
			if (a.getCategory().equals(category))
				filteredAnnotations.add(a);
		}
		return filteredAnnotations;
	}

	public Set<String> getAnnotationCategories() {
		Set<String> distinctCategories = Sets.newTreeSet();
		if (this.annotations != null) {
			for (Annotation a : annotations) {
				String category = a.getCategory();
				if (!distinctCategories.contains(category)) {
					distinctCategories.add(category);
				}
			}
		}
		return distinctCategories;
	}

	public List<PeptideMapping> getPeptideMappings() {
		return peptideMappings;
	}

	public void setPeptideMappings(List<PeptideMapping> peptideMappings) {
		this.peptideMappings = peptideMappings;
	}

	public List<AntibodyMapping> getAntibodyMappings() {
		return antibodyMappings;
	}

	public void setAntibodyMappings(List<AntibodyMapping> antibodyMappings) {
		this.antibodyMappings = antibodyMappings;
	}

	public List<GenomicMapping> getGenomicMappings() {
		return genomicMappings;
	}

	public void setGenomicMappings(List<GenomicMapping> genomicMappings) {
		this.genomicMappings = genomicMappings;
	}

	public List<Interaction> getInteractions() {
		return interactions;
	}

	public void setInteractions(List<Interaction> interactions) {
		this.interactions = interactions;
	}

	public void setAnnotations(List<Annotation> annotations) {
		this.annotations = annotations;
	}

	public void setEnzymes(List<Terminology> enzymes) {
		this.enzymes=enzymes;
	}

	public List<Terminology> getEnzymes(){
		return enzymes;
	}
}
