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

	private static final long serialVersionUID = 4L;

    private String dataRelease;
	private Summary summary;
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

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonPropertyOrder({
			"chromosome",
			"entryCount",
			"geneCount",
			"proteinLevelEntryCount",
			"transcriptLevelEntryCount",
			"homologyEntryCount",
			"predictedEntryCount",
			"uncertainEntryCount"
	})
	public static class Summary implements Serializable {

		private static final long serialVersionUID = 3L;

		private String chromosome;
		private int entryCount;
		private int entryReportCount;
		private Map<ProteinExistence, Integer> countByProteinEvidence = new HashMap<>(5);

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

		@JsonProperty("geneCount")
		public int getEntryReportCount() {
			return entryReportCount;
		}

		public void setEntryReportCount(int entryReportCount) {
			this.entryReportCount = entryReportCount;
		}

		public void setEntryCount(ProteinExistence level, int count) {

			countByProteinEvidence.put(level, count);
		}

		@JsonProperty("proteinLevelEntryCount")
		public int getProteinLevelEntryCount() {

			return countProteinEvidenceEntries(ProteinExistence.PROTEIN_LEVEL);
		}

		@JsonProperty("transcriptLevelEntryCount")
		public int getTranscriptLevelEntryCount() {

			return countProteinEvidenceEntries(ProteinExistence.TRANSCRIPT_LEVEL);
		}

		@JsonProperty("homologyEntryCount")
		public int getHomologyLevelEntryCount() {

			return countProteinEvidenceEntries(ProteinExistence.HOMOLOGY);
		}

		@JsonProperty("predictedEntryCount")
		public int getPredictedLevelEntryCount() {

			return countProteinEvidenceEntries(ProteinExistence.PREDICTED);
		}

		@JsonProperty("uncertainEntryCount")
		public int getUncertainLevelEntryCount() {

			return countProteinEvidenceEntries(ProteinExistence.UNCERTAIN);
		}

		private int countProteinEvidenceEntries(ProteinExistence level) {
			return countByProteinEvidence.getOrDefault(level, 0);
		}
	}

}
