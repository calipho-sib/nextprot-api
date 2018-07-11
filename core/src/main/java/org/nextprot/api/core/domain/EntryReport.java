package org.nextprot.api.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.nextprot.api.commons.exception.NextProtException;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static org.nextprot.api.core.domain.ChromosomalLocation.comparePosition;
import static org.nextprot.api.core.domain.EntryReport.*;
import static org.nextprot.api.core.domain.EntryReportStats.*;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	GENE_NAME,
	CHROMOSOMAL_LOCATION,
	GENE_START_POSITION,
	GENE_END_POSITION,
	CODING_STRAND,
	ENTRY_ACCESSION,
	PROTEIN_EXISTENCE,
	IS_PROTEOMICS,
	IS_ANTIBODY,
	IS_3D,
	IS_DISEASE,
	ISOFORM_COUNT,
	VARIANT_COUNT,
	PTM_COUNT,
	CURATED_PUBLICATION_COUNT,
	ADDITIONAL_PUBLICATION_COUNT,
	PATENT_COUNT,
	SUBMISSION_COUNT,
	WEB_RESOURCE_COUNT,
	ENTRY_DESCRIPTION
})
public class EntryReport implements Serializable {

	private static final long serialVersionUID = 6L;

	private static final String SENSE_CODING_STRAND = "forward";
	private static final String ANTISENSE_CODING_STRAND = "reverse";
	private static final String UNKNOWN_CODING_STRAND = "-"; // unknown

	public static final String GENE_NAME = "geneName";
	public static final String CODING_STRAND = "codingStrand";
	public static final String CHROMOSOMAL_LOCATION = "chromosomalLocation";
	public static final String GENE_START_POSITION = "geneStartPosition";
	public static final String GENE_END_POSITION = "geneEndPosition";

	private String accession;
	private String description;

	private ChromosomalLocation chromosomalLocation;
	private ProteinExistence proteinExistence;
	private Map<String, Boolean> propertyTests = new HashMap<>(4);
	private Map<String, Integer> propertyCounts = new HashMap<>(4);

	public EntryReport duplicateThenSetChromosomalLocation(ChromosomalLocation chromosomalLocation) {

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
		String recommendedName = chromosomalLocation.getRecommendedName();

		return ("unknown".equals(recommendedName)) ? "-" : recommendedName;
	}

	public static String getValidGeneNameValue(String geneName) {

		if (geneName != null && !geneName.isEmpty() && !"unknown".equals(geneName)) {
			return geneName;
		}
		return "-";
	}

	@JsonProperty(CODING_STRAND)
	public String getCodingStrand() {
		if (chromosomalLocation.getStrand() > 0) return SENSE_CODING_STRAND;
		if (chromosomalLocation.getStrand() < 0) return ANTISENSE_CODING_STRAND;
		return UNKNOWN_CODING_STRAND;
	}

	@JsonProperty(GENE_START_POSITION)
	public String getGeneStartPosition() {
		return getPositionOrUndefined(chromosomalLocation.getFirstPosition());
	}

	@JsonProperty(GENE_END_POSITION)
	public String getGeneEndPosition() {
		return getPositionOrUndefined(chromosomalLocation.getLastPosition());
	}

	private String getPositionOrUndefined(int pos) {

		return (pos > 0) ? String.valueOf(pos) : "-";
	}

	@JsonProperty(PROTEIN_EXISTENCE)
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

	@JsonProperty(CURATED_PUBLICATION_COUNT)
	public int countCuratedPublications() {

		return propertyCounts.get(CURATED_PUBLICATION_COUNT);
	}

	@JsonProperty(ADDITIONAL_PUBLICATION_COUNT)
	public int countAdditionalPublications() {

		return propertyCounts.get(ADDITIONAL_PUBLICATION_COUNT);
	}

	@JsonProperty(PATENT_COUNT)
	public int countPatents() {

		return propertyCounts.get(PATENT_COUNT);
	}

	@JsonProperty(SUBMISSION_COUNT)
	public int countSubmissions() {

		return propertyCounts.get(SUBMISSION_COUNT);
	}

	@JsonProperty(WEB_RESOURCE_COUNT)
	public int countWebResources() {

		return propertyCounts.get(WEB_RESOURCE_COUNT);
	}

	@JsonProperty(CHROMOSOMAL_LOCATION)
	public String getChromosomalLocation() {

		return ChromosomalLocation.toString(chromosomalLocation);
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

	public void setProteinExistence(ProteinExistence proteinExistence) {
		this.proteinExistence = proteinExistence;
	}

	public void setChromosomalLocation(ChromosomalLocation chromosomalLocation) {
		this.chromosomalLocation = chromosomalLocation;
	}

	/**
	 * Factory creates a new Comparator that compare EntryReport by gene position first then by chromosomal band position
	 * then by gene name and then by accession
	 * @return a Comparator of EntryReport objects
	 */
	public static Comparator<EntryReport> newByChromosomalPositionComparator() {

		return new EntryReport.ByGeneLocationComparator()
				.thenComparing((er -> {
					try {
						return ChromosomalLocation.fromString(er.getChromosomalLocation());
					} catch (ParseException e) {
						throw new NextProtException("Internal error: cannot sort EntryReport" + e.getMessage());
					}
				}), new ChromosomalLocation.ByChromosomalBandLocationComparator())
				.thenComparing(EntryReport::getGeneName)
				.thenComparing(EntryReport::getAccession);
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

	private static class ByGeneLocationComparator implements Comparator<EntryReport> {

		@Override
		public int compare(EntryReport er1, EntryReport er2) {

			int cmp = comparePosition(er1.getGeneStartPosition(), er2.getGeneStartPosition(), "-",
					Comparator.comparingInt(Integer::parseInt));

			if (cmp == 0)
				cmp = comparePosition(er1.getGeneEndPosition(), er2.getGeneEndPosition(), "-",
						Comparator.comparingInt(Integer::parseInt));

			return cmp;
		}
	}
}
