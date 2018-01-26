package org.nextprot.api.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.utils.KeyValueRepresentation;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@JsonInclude(Include.NON_NULL)
public class Entry implements KeyValueRepresentation {

	private EntryProperties properties;

	private String uniqueName;

	private Overview overview;

	private List<Publication> publications;

	private List<DbXref> xrefs;

	private List<Identifier> identifiers;

	private List<ChromosomalLocation> chromosomalLocations;

	private List<Isoform> isoforms;

	private List<Annotation> annotations;

	private List<GenomicMapping> genomicMappings;

	private List<Interaction> interactions;

	private List<CvTerm> enzymes;

	private List<ExperimentalContext> experimentalContexts;

	public List<ExperimentalContext> getExperimentalContexts() {
		return experimentalContexts;
	}

	public Optional<ExperimentalContext> getExperimentalContext(long id) {

		return experimentalContexts.stream()
				.filter(ec -> ec.getContextId() == id)
				.findAny();
	}

	public void setExperimentalContexts(List<ExperimentalContext> experimentalContexts) {
		this.experimentalContexts = experimentalContexts;
	}

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

	/**
	 * @return the dbxref with given id
	 */
	public Optional<DbXref> getXref(long id) {
		return xrefs.stream()
				.filter(xr -> xr.getDbXrefId() == id)
				.findFirst();
	}

	public void setXrefs(List<DbXref> xrefs) {
		this.xrefs = xrefs;
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

	/*
	 * Should use get annotations by category or @json null 
	 */
    @JsonIgnore
	public List<Annotation> getAnnotations() {
		return annotations;
	}

	public Map<String, List<Annotation>> getAnnotationsByCategory() {
		if(annotations == null) return null;
		
		return annotations.stream().collect(Collectors.groupingBy(a -> StringUtils.camelToKebabCase(a.getApiTypeName())));
	}

	public List<Annotation> getAnnotationsByCategory(AnnotationCategory category) {
		if(annotations == null) return new ArrayList<>();

		return annotations.stream()
				.filter(a -> a.getAPICategory() == category)
				.collect(Collectors.toList());
	}

	public List<Annotation> getAnnotationsByIsoform(String isoform) {
		return Entry.filterByIsoform(annotations, isoform);
	}

	public List<Interaction> getInteractionsByIsoform(String isoform) {
		return Entry.filterByIsoform(interactions, isoform);
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

	public void setEnzymes(List<CvTerm> enzymes) {
		this.enzymes = enzymes;
	}

	public List<CvTerm> getEnzymes() {
		return enzymes;
	}

	public EntryProperties getProperties() {
		return properties;
	}

	public void setProperties(EntryProperties properties) {
		this.properties = properties;
	}

	/**
	 * Filter a elements specific of the given isoform
	 * 
	 * @param tList
	 *            the list to filter
	 * @param isoform
	 *            the isoform filter applied to the list
	 * @param <T>
	 *            the element type that implement the IsoformSpecific interface
	 * @return a filtered list
	 */
	private static <T extends IsoformSpecific> List<T> filterByIsoform(List<T> tList, String isoform) {

		List<T> list = new ArrayList<>();

		if (tList != null) {
			for (T t : tList) {
				if (t.isSpecificForIsoform(isoform))
					list.add(t);
			}
		}

		return list;
	}

	@Override
	public String toKeyValueString() {
		StringBuilder sb = new StringBuilder();
		sb.append("entry=" + this.uniqueName + ";");
		sb.append("annotations-count=" + ((this.annotations != null) ? this.annotations.size() : 0) + ";");
		sb.append("publications-count=" + ((this.publications != null) ? this.publications.size() : 0) + ";");
		sb.append("xrefs-count=" + ((this.xrefs != null) ? this.xrefs.size() : 0) + ";");
		return sb.toString();
	}

}
