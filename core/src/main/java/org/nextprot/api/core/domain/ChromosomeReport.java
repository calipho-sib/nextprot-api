package org.nextprot.api.core.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "dataRelease",
		"summary",
		"entryReports"
})
public class ChromosomeReport implements Serializable {

	private static final long serialVersionUID = 3L;

    private String dataRelease;
	private Summary summary;
	private EntryCountByProteinEvidence entryCountByProteinEvidence;
	private List<EntryReport> entryReports;

	public Summary getSummary() {
		return summary;
	}

	public void setSummary(Summary summary) {
		this.summary = summary;
	}

	public List<EntryReport> getEntryReports() {
		return entryReports;
	}

	public void setEntryReports(List<EntryReport> entryReports) {
		this.entryReports = entryReports;
	}

    public String getDataRelease() {
        return dataRelease;
    }

    public void setDataRelease(String dataRelease) {
        this.dataRelease = dataRelease;
    }

	public EntryCountByProteinEvidence getEntryCountByProteinEvidence() {
		return entryCountByProteinEvidence;
	}

	public void setEntryCountByProteinEvidence(EntryCountByProteinEvidence entryCountByProteinEvidence) {
		this.entryCountByProteinEvidence = entryCountByProteinEvidence;
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonPropertyOrder({
			"chromosome",
			"entryCount",
			"geneCount"
	})
	public static class Summary implements Serializable {

		private static final long serialVersionUID = 2L;

		private String chromosome;
		private int entryCount;
		private int geneCount;

		public String getChromosome() {
			return chromosome;
		}

		public void setChromosome(String chromosome) {
			this.chromosome = chromosome;
		}

		public int getEntryCount() {
			return entryCount;
		}

		public void setEntryCount(int entryCount) {
			this.entryCount = entryCount;
		}

		public int getGeneCount() {
			return geneCount;
		}

		public void setGeneCount(int geneCount) {
			this.geneCount = geneCount;
		}
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonPropertyOrder({
			"entry count",
			"protein level (PE1)",
			"transcript level (PE2)",
			"homology (PE3)",
			"predicted (PE4)",
			"uncertain (PE5)",
			"awaiting protein validation (P2+P3+P4)"
	})
	public static class EntryCountByProteinEvidence implements Serializable {

		private static final long serialVersionUID = 1L;

		private Map<ProteinExistenceLevel, Integer> countByProteinEvidence = new HashMap<>(5);

		public void setEntryCount(ProteinExistenceLevel level, int count) {

			countByProteinEvidence.put(level, count);
		}

		@JsonProperty("protein level (PE1)")
		public int countProteinLevelEntries() {

			return countProteinEvidenceEntries(ProteinExistenceLevel.PROTEIN_LEVEL);
		}

		@JsonProperty("transcript level (PE2)")
		public int countTranscriptLevelEntries() {

			return countProteinEvidenceEntries(ProteinExistenceLevel.TRANSCRIPT_LEVEL);
		}

		@JsonProperty("homology (PE3)")
		public int countHomologyLevelEntries() {

			return countProteinEvidenceEntries(ProteinExistenceLevel.HOMOLOGY);
		}

		@JsonProperty("predicted (PE4)")
		public int countPredictedLevelEntries() {

			return countProteinEvidenceEntries(ProteinExistenceLevel.PREDICTED);
		}

		@JsonProperty("uncertain (PE5)")
		public int countUncertainLevelEntries() {

			return countProteinEvidenceEntries(ProteinExistenceLevel.UNCERTAIN);
		}

		@JsonProperty("awaiting protein validation (P2+P3+P4)")
		public int countAwaitingProteinValidationevelEntries() {

			return countTranscriptLevelEntries() + countHomologyLevelEntries() + countPredictedLevelEntries();
		}

		@JsonProperty("entry count")
		public int countEntryCount() {

			return countAwaitingProteinValidationevelEntries() + countProteinLevelEntries() + countUncertainLevelEntries();
		}

		private int countProteinEvidenceEntries(ProteinExistenceLevel level) {
			return countByProteinEvidence.getOrDefault(level, 0);
		}
	}

}
