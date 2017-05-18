package org.nextprot.api.core.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
		"summary",
		"entryReports"
})
public class ChromosomeReport implements Serializable {

	private static final long serialVersionUID = 2L;

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

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonPropertyOrder({
			"chromosome",
			"dataRelease",
			"entryCount",
			"geneCount"
	})
	public static class Summary implements Serializable {

		private static final long serialVersionUID = 2L;

		private String chromosome;
		private String dataRelease;
		private Count count;

		public String getChromosome() {
			return chromosome;
		}

		public void setChromosome(String chromosome) {
			this.chromosome = chromosome;
		}

		public String getDataRelease() {
			return dataRelease;
		}

		public void setDataRelease(String dataRelease) {
			this.dataRelease = dataRelease;
		}

		public Count getCount() {
			return count;
		}

		public void setCount(Count count) {
			this.count = count;
		}

		public static class Count implements Serializable {

			private static final long serialVersionUID = 1L;

			private int entryCount;
			private int geneCount;

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
	}
}
