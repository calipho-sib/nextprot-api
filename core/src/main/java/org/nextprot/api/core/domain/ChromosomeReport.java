package org.nextprot.api.core.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "dataRelease",
		"summary",
		"entryReports"
})
public class ChromosomeReport implements Serializable {

	private static final long serialVersionUID = 2L;

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
			"geneCount"
	})
	public static class Summary implements Serializable {

		private static final long serialVersionUID = 2L;

		private String chromosome;
		private int entryCount;
		private int entryReportCount;

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
	}
}
