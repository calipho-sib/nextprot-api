package org.nextprot.api.core.service;

import com.google.common.collect.Sets;
import org.junit.Test;
import org.nextprot.api.core.domain.ChromosomeReport;
import org.nextprot.api.core.domain.EntryReport;
import org.nextprot.api.core.service.export.io.ChromosomeReportTXTReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;


public class ChromosomeEntryReportIntegrationTest {

	@Test
	public void chromosomeEntryReportsShouldMatchFTPReports() throws Exception {

        PrintWriter pw = new PrintWriter("/tmp/ftpVSapiChromosomeEntryReports.tsv");

		pw.write(DifferenceAnalyser.reportAllChromosomes());

		pw.close();
	}

	private static class DifferenceAnalyser {

		private final String chromosome;
		private ChromosomeReport chromosomeReportFromFTP;
		private ChromosomeReport chromosomeReportFromAPI;
		private Differences differences;

        private DifferenceAnalyser(String chromosome) throws IOException, ParseException {
			this.chromosome = chromosome;
			readReports();
			calcDifferences();
		}

		private void readReports() throws IOException, ParseException {

			ChromosomeReportTXTReader reader = new ChromosomeReportTXTReader();

			URL ftpUrl = new URL("ftp://ftp.nextprot.org/pub/current_release/chr_reports/nextprot_chromosome_"+chromosome+".txt");
			chromosomeReportFromFTP = reader.read(new InputStreamReader(ftpUrl.openStream()));

			URL apiURL = new URL("http://build-api.nextprot.org/export/reports/chromosome/"+chromosome+".txt");
			chromosomeReportFromAPI = reader.read(new InputStreamReader(apiURL.openStream()));
		}

		private Differences calcDifferences() {

			differences = new Differences(chromosome);

			// 1. row count diffs
			differences.setRowNumberInApi(chromosomeReportFromAPI.getEntryReports().size());
			differences.setRowNumberInFTP(chromosomeReportFromFTP.getEntryReports().size());

			// 2. Summary diffs
			calcSummaryDiffs();

			// 3. Distinct entries
			Set<String> entriesInAPI = chromosomeReportFromAPI.getEntryReports().stream()
					.map(EntryReport::getAccession)
					.collect(Collectors.toSet());

			Set<String> entriesInFTP = chromosomeReportFromFTP.getEntryReports().stream()
					.map(EntryReport::getAccession)
					.collect(Collectors.toSet());

			differences.addAllDistinctAccsInAPI(Sets.difference(entriesInAPI, entriesInFTP));
			differences.addAllDistinctAccsInFTP(Sets.difference(entriesInFTP, entriesInAPI));

			// 4. Distinct genes
			Set<String> genesInAPI = chromosomeReportFromAPI.getEntryReports().stream()
					.map(EntryReport::getGeneName)
					.collect(Collectors.toSet());

			Set<String> genesInFTP = chromosomeReportFromFTP.getEntryReports().stream()
					.map(EntryReport::getGeneName)
					.collect(Collectors.toSet());

			differences.addAllDistinctGenesInAPI(Sets.difference(genesInAPI, genesInFTP));
			differences.addAllDisctinctGenesInFTP(Sets.difference(genesInFTP, genesInAPI));

			return differences;
		}

		private void calcSummaryDiffs() {

			ChromosomeReport.Summary summaryFTP = chromosomeReportFromFTP.getSummary();
			ChromosomeReport.Summary summaryAPI = chromosomeReportFromAPI.getSummary();

			differences.setDeltaEntryCount(Math.abs(summaryAPI.getEntryCount() - summaryFTP.getEntryCount()));
			differences.setDeltaGeneCount(Math.abs(summaryAPI.getGeneCount() - summaryFTP.getGeneCount()));
		}

		Differences getDifferences() {

			return differences;
		}

		public static String reportAllChromosomes() throws IOException, ParseException {

			StringBuilder sb = new StringBuilder();

			List<String> allChromosomes = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11",
					"12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "X", "Y", "MT", "unknown");

			sb.append(Differences.getHeaders().stream().collect(Collectors.joining("\t"))).append("\n");
			for (String chromosome : allChromosomes) {

				DifferenceAnalyser analyser = new DifferenceAnalyser(chromosome);
				sb.append(analyser.getDifferences().getValues().stream().collect(Collectors.joining("\t"))).append("\n");
			}

			return sb.toString();
		}
	}

	private static class Differences {

		private final String chromosome;

		private final Set<String> distinctEntryReportAccsInAPI = new HashSet<>();
		private final Set<String> distinctEntryReportAccsInFTP = new HashSet<>();
		private final Set<String> distinctEntryReportGenesInAPI = new HashSet<>();
		private final Set<String> distinctEntryReportGenesInFTP = new HashSet<>();
		private int rowNumberInApi;
		private int rowNumberInFTP;
		private int deltaEntryCount;
		private int deltaGeneCount;

        private Differences(String chromosome) {
			this.chromosome = chromosome;
		}

		public void addAllDistinctAccsInAPI(Collection<String> notInAPIAccs) {
			distinctEntryReportAccsInAPI.addAll(notInAPIAccs);
		}

		public void addAllDistinctAccsInFTP(Collection<String> notInFTPAccs) {

			distinctEntryReportAccsInFTP.addAll(notInFTPAccs);
		}

		public void addAllDistinctGenesInAPI(Collection<String> notInAPIGenes) {
			distinctEntryReportGenesInAPI.addAll(notInAPIGenes);
		}

		public void addAllDisctinctGenesInFTP(Collection<String> notInFTPGenes) {

			distinctEntryReportGenesInFTP.addAll(notInFTPGenes);
		}

		public Set<String> getDistinctEntryReportAccsInAPI() {
			return distinctEntryReportAccsInAPI;
		}

		public Set<String> getDistinctEntryReportAccsInFTP() {
			return distinctEntryReportAccsInFTP;
		}

		public Set<String> getDistinctEntryReportGenesInAPI() {
			return distinctEntryReportGenesInAPI;
		}

		public Set<String> getDistinctEntryReportGenesInFTP() {
			return distinctEntryReportGenesInFTP;
		}

		public int getRowNumberInApi() {
			return rowNumberInApi;
		}

		public int getRowNumberInFTP() {
			return rowNumberInFTP;
		}

		public void setRowNumberInApi(int rowNumberInApi) {
			this.rowNumberInApi = rowNumberInApi;
		}

		public void setRowNumberInFTP(int rowNumberInFTP) {
			this.rowNumberInFTP = rowNumberInFTP;
		}

		public int getDeltaEntryCount() {
			return deltaEntryCount;
		}

		public void setDeltaEntryCount(int deltaEntryCount) {
			this.deltaEntryCount = deltaEntryCount;
		}

		public int getDeltaGeneCount() {
			return deltaGeneCount;
		}

		public void setDeltaGeneCount(int deltaGeneCount) {
			this.deltaGeneCount = deltaGeneCount;
		}

		public static List<String> getHeaders() {
			return Arrays.asList("chromosome",
					"delta entry count (abs(api-ftp))", "delta gene count (abs(api-ftp))",
					"row count (api)", "row count (ftp)",
					"distinct entry count (api)", "distinct entry count (ftp)",
					"distinct gene count (api)", "distinct gene count (ftp)"
			);
		}

		public List<String> getValues() {

			return Arrays.asList(chromosome,
					String.valueOf(deltaEntryCount), String.valueOf(deltaGeneCount),
					String.valueOf(rowNumberInApi), String.valueOf(rowNumberInFTP),
					String.valueOf(distinctEntryReportAccsInAPI.size()), String.valueOf(distinctEntryReportAccsInFTP.size()),
					String.valueOf(distinctEntryReportGenesInAPI.size()), String.valueOf(distinctEntryReportGenesInFTP.size()));
		}
	}
}

