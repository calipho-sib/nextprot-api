package org.nextprot.api.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static org.nextprot.api.core.domain.EntryReport.*;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	GENE_NAME,
	CHROMOSOMAL_LOCATION,
	GENE_START_POSITION,
	GENE_END_POSITION,
	ENTRY_ACCESSION,
	PROTEIN_EXISTENCE_LEVEL,
	IS_PROTEOMICS,
	IS_ANTIBODY,
	IS_3D,
	IS_DISEASE,
	ISOFORM_COUNT,
	VARIANT_COUNT,
	PTM_COUNT,
	ENTRY_DESCRIPTION
})
public class EntryReport implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String GENE_NAME = "geneName";
	public static final String CHROMOSOMAL_LOCATION = "chromosomalLocation";
	public static final String GENE_START_POSITION = "geneStartPosition";
	public static final String GENE_END_POSITION = "geneEndPosition";
	public static final String ENTRY_ACCESSION = "entryAccession";
	public static final String ENTRY_DESCRIPTION = "entryDescription";
	public static final String PROTEIN_EXISTENCE_LEVEL = "proteinExistence";
	public static final String IS_PROTEOMICS = "proteomics";
	public static final String IS_ANTIBODY = "antibody";
	public static final String IS_3D = "3D";
	public static final String IS_DISEASE = "disease";
	public static final String ISOFORM_COUNT = "isoforms";
	public static final String VARIANT_COUNT = "variants";
	public static final String PTM_COUNT = "ptms";

	private String accession;
	private String description;

	private ChromosomalLocation chromosomalLocation;
	private ProteinExistenceLevel proteinExistence;
	private Map<String, Boolean> propertyTests = new HashMap<>(4);
	private Map<String, Integer> propertyCounts = new HashMap<>(4);

	public EntryReport copyThenSetChromosomalLocation(ChromosomalLocation chromosomalLocation) {

		EntryReport copy = new EntryReport();

		copy.setAccession(accession);
		copy.setDescription(description);
		copy.setProteinExistence(proteinExistence);
		copy.propertyTests = new HashMap<>(propertyTests);
		copy.propertyCounts = new HashMap<>(propertyCounts);
		copy.setChromosomalLocation(chromosomalLocation);

		return copy;
	}

	@JsonIgnore
	public String getChromosome() {
		return chromosomalLocation.getChromosome();
	}

	@JsonProperty(ENTRY_ACCESSION)
	public String getAccession() {
		return accession;
	}

	@JsonProperty(ENTRY_DESCRIPTION)
	public String getDescription() {
		return description;
	}

	@JsonProperty(GENE_NAME)
	public String getGeneName() {
		return chromosomalLocation.getRecommendedName();
	}

	@JsonProperty(GENE_START_POSITION)
	public int getGeneStartPosition() {
		return getPositionOrUndefined(chromosomalLocation.getFirstPosition());
	}

	@JsonProperty(GENE_END_POSITION)
	public int getGeneEndPosition() {
		return getPositionOrUndefined(chromosomalLocation.getLastPosition());
	}

	private int getPositionOrUndefined(int pos) {

		return (pos > 0) ? pos : -1;
	}

	@JsonProperty(PROTEIN_EXISTENCE_LEVEL)
	public String getProteinExistence() {
		return proteinExistence.getName();
	}

	@JsonProperty(IS_PROTEOMICS)
	public boolean isProteomics() {

		return testProperty(IS_PROTEOMICS);
	}

	@JsonProperty(IS_ANTIBODY)
	public boolean isAntibody() {

		return testProperty(IS_ANTIBODY);
	}

	@JsonProperty(IS_3D)
	public boolean is3D() {

		return testProperty(IS_3D);
	}

	@JsonProperty(IS_DISEASE)
	public boolean isDisease() {

		return testProperty(IS_DISEASE);
	}

	@JsonProperty(ISOFORM_COUNT)
	public int countIsoforms() {

		return propertyCounts.get(ISOFORM_COUNT);
	}

	@JsonProperty(VARIANT_COUNT)
	public int countVariants() {

		return propertyCounts.get(VARIANT_COUNT);
	}

	@JsonProperty(PTM_COUNT)
	public int countPTMs() {

		return propertyCounts.get(PTM_COUNT);
	}

	@JsonProperty(CHROMOSOMAL_LOCATION)
	public String getChromosomalLocation() {

		StringBuilder sb = new StringBuilder();

		String chromosome = chromosomalLocation.getChromosome();
		String band = chromosomalLocation.getBand();

		if (chromosome != null && !"unknown".equals(chromosome)) {
			sb.append(chromosome);
		}
		if (band != null && !"unknown".equals(band)) {
			sb.append(band);
		}

		if (sb.length() == 0) {
			sb.append("-");
		}

		return sb.toString();
	}

	public boolean testProperty(String testName) {

		return propertyTests.containsKey(testName) && propertyTests.get(testName);
	}

	public int count(String countName) {

		return propertyCounts.getOrDefault(countName, 0);
	}

	public void setPropertyTest(String testName, boolean bool) {

		propertyTests.put(testName, bool);
	}

	public void setPropertyCount(String countName, int count) {

		propertyCounts.put(countName, count);
	}

	public void setAccession(String accession) {
		this.accession = accession;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setProteinExistence(ProteinExistenceLevel proteinExistence) {
		this.proteinExistence = proteinExistence;
	}

	public void setChromosomalLocation(ChromosomalLocation chromosomalLocation) {
		this.chromosomalLocation = chromosomalLocation;
	}

	public static class ByGenePosComparator implements Comparator<EntryReport> {

		@Override
		public int compare(EntryReport er1, EntryReport er2) {

			int s1 = er1.getGeneStartPosition();
			int s2 = er2.getGeneStartPosition();

			// EntryReport with undefined positions comes last
			if (s1 < 0) {
				return 1;
			}
			if (s2 < 0) {
				return -1;
			}

			return Integer.compare(s1, s2);
		}
	}

	public static class ByChromosomeComparator implements Comparator<EntryReport> {

		@Override
		public int compare(EntryReport er1, EntryReport er2) {

			String chr1 = er1.getChromosome();
			String chr2 = er2.getChromosome();

			boolean isChr1Int = chr1.matches("\\d+");
			boolean isChr2Int = chr2.matches("\\d+");

			if (isChr1Int && isChr2Int) {
				return Integer.compare(Integer.parseInt(chr1), Integer.parseInt(chr2));
			}
			else if (isChr1Int) {
				return -1;
			}
			else if (isChr2Int) {
				return 1;
			}
			else {
				return chr1.compareTo(chr2);
			}
		}
	}
}
