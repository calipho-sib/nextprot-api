package org.nextprot.api.core.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.nextprot.api.commons.utils.KeyValueRepresentation;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.IsoformAnnotation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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

	@Deprecated
	private List<Annotation> annotations;

	private List<IsoformAnnotation> isoformAnnotations = new ArrayList<>();

	private List<PeptideMapping> peptideMappings;

	private List<PeptideMapping> srmPeptideMappings;

	private List<AntibodyMapping> antibodyMappings;

	private List<GenomicMapping> genomicMappings;

	private List<Interaction> interactions;

	private List<CvTerm> enzymes;

	private List<ExperimentalContext> experimentalContexts;

	public List<ExperimentalContext> getExperimentalContexts() {
		return experimentalContexts;
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

	public String getProteinExistenceInfo() {
		if (this.properties != null) {
			return this.properties.getProteinExistenceInfo();
		}
		return null;
	}

	public String getProteinExistence() {
		if (this.overview != null) {
			return this.overview.getProteinExistence();
		}
		return null;
	}

	public Integer getProteinExistenceLevel() {
		if (this.overview != null) {
			return this.overview.getProteinExistenceLevel();
		}
		return null;
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
		return Entry.filterByIsoform(antibodyMappings, isoform);
	}

	public List<PeptideMapping> getPeptidesByIsoform(String isoform) {
		return Entry.filterByIsoform(peptideMappings, isoform);
	}

	public List<PeptideMapping> getSrmPeptidesByIsoform(String isoform) {
		return Entry.filterByIsoform(srmPeptideMappings, isoform);
	}

	public List<Annotation> getAnnotationsByIsoform(String isoform) {
		return Entry.filterByIsoform(annotations, isoform);
	}

	public List<Interaction> getInteractionsByIsoform(String isoform) {
		return Entry.filterByIsoform(interactions, isoform);
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

	public void setEnzymes(List<CvTerm> enzymes) {
		this.enzymes = enzymes;
	}

	public List<CvTerm> getEnzymes() {
		return enzymes;
	}

	public List<PeptideMapping> getSrmPeptideMappings() {
		return srmPeptideMappings;
	}

	public void setSrmPeptideMappings(List<PeptideMapping> srmPeptideMappings) {
		this.srmPeptideMappings = srmPeptideMappings;
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

	/*public Map<String, Map<String, List<IsoformAnnotation>>> getProteoformAnnotations() {

		return isoformAnnotations.stream().filter(ia -> (ia.getSubjectComponents() != null && !ia.getSubjectComponents().isEmpty())).
				collect( 
						Collectors.groupingBy(
						IsoformAnnotation::getSubjectName, TreeMap::new, Collectors.groupingBy(
								IsoformAnnotation::getKebabCategoryName,  TreeMap::new, Collectors.toList())));
	}

	public Map<String, Map<String, List<IsoformAnnotation>>> getAnnotationsByIsoformAndCategory() {

		return isoformAnnotations.stream()
				.filter(ia -> (ia.getSubjectComponents() == null || ia.getSubjectComponents().isEmpty()))
				.collect(Collectors.groupingBy(
						IsoformAnnotation::getSubjectName, TreeMap::new, Collectors.groupingBy(
								IsoformAnnotation::getKebabCategoryName,  TreeMap::new, Collectors.toList())));
	}
	*/

	public Map<String, Map<String, List<Annotation>>> getProteoformAnnotations() {

		return annotations.stream()
				.filter(a -> (a.getSubjectComponents() != null && !a.getSubjectComponents().isEmpty()))
				.collect(Collectors.groupingBy(Annotation::getSubjectName, TreeMap::new, Collectors.groupingBy(
						Annotation::getKebabCategoryName, TreeMap::new, Collectors.toList())));
	}

	public Map<String, Map<String, List<Annotation>>> getAnnotationsByIsoformAndCategory() {

		return annotations.stream()
				.filter(a -> (a.getSubjectComponents() == null || a.getSubjectComponents().isEmpty()))
				.collect(Collectors.groupingBy(Annotation::getSubjectName, TreeMap::new, Collectors.groupingBy(
						Annotation::getKebabCategoryName,  TreeMap::new, Collectors.toList())));
	}

	public void addIsoformAnnotations(List<IsoformAnnotation> annotations) {
		this.isoformAnnotations.addAll(annotations);
	}

}
