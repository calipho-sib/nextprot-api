package org.nextprot.api.core.service;

import com.google.common.collect.Sets;
import org.apache.lucene.util.Counter;
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

			readReportFromURLs(
					"ftp://ftp.nextprot.org/pub/current_release/chr_reports/nextprot_chromosome_"+chromosome+".txt",
					"http://build-api.nextprot.org/chromosome-report/export/"+chromosome
			);

			calcDifferences();
		}

		private void readReportFromURLs(String ftp, String api) throws IOException, ParseException {

			ChromosomeReportTXTReader reader = new ChromosomeReportTXTReader();

			chromosomeReportFromFTP = reader.read(new InputStreamReader(new URL(ftp).openStream()));
			chromosomeReportFromAPI = reader.read(new InputStreamReader(new URL(api).openStream()));
		}

		private Differences calcDifferences() {

			differences = new Differences(this);

			// 1. row count diffs
			calcReportDifferences();

			// 2. Summary diffs
			calcReportSummaryDifferences();

			// 3. Distinct entries accessions
			calcDistinctEntryAccessions();

			// 4. Distinct genes names
			calcDistinctGeneNames();

			// 5. Gene name duplication delta
			calcDuplicateGeneNamesDifferences();

			return differences;
		}

		private void calcReportDifferences() {

			differences.setCountReportsInApi(chromosomeReportFromAPI.getEntryReports().size());
			differences.setCountReportsInFTP(chromosomeReportFromFTP.getEntryReports().size());
        }

		private void calcReportSummaryDifferences() {

			ChromosomeReport.Summary summaryFTP = chromosomeReportFromFTP.getSummary();
			ChromosomeReport.Summary summaryAPI = chromosomeReportFromAPI.getSummary();

			differences.setCountEntryDelta(Math.abs(summaryAPI.getEntryCount() - summaryFTP.getEntryCount()));
			differences.setCountGeneDelta(Math.abs(summaryAPI.getGeneCount() - summaryFTP.getGeneCount()));
		}

		private void calcDistinctEntryAccessions() {

			Set<String> entriesInAPI = chromosomeReportFromAPI.getEntryReports().stream()
					.map(EntryReport::getAccession)
					.collect(Collectors.toSet());

			Set<String> entriesInFTP = chromosomeReportFromFTP.getEntryReports().stream()
					.map(EntryReport::getAccession)
					.collect(Collectors.toSet());

			differences.addAllDistinctAccsInAPI(Sets.difference(entriesInAPI, entriesInFTP));
			differences.addAllDistinctAccsInFTP(Sets.difference(entriesInFTP, entriesInAPI));
		}

		private void calcDistinctGeneNames() {

			Set<String> genesInAPI = chromosomeReportFromAPI.getEntryReports().stream()
					.map(EntryReport::getGeneName)
					.collect(Collectors.toSet());

			Set<String> genesInFTP = chromosomeReportFromFTP.getEntryReports().stream()
					.map(EntryReport::getGeneName)
					.collect(Collectors.toSet());

			differences.addAllDistinctGenesInAPI(Sets.difference(genesInAPI, genesInFTP));
			differences.addAllDisctinctGenesInFTP(Sets.difference(genesInFTP, genesInAPI));
		}

		private void calcDuplicateGeneNamesDifferences() {

			Map<String, Integer> reportCountByGeneNameFromFTP = getEntryReportCountByGeneName(chromosomeReportFromFTP);
			Map<String, Integer> reportCountByGeneNameFromAPI = getEntryReportCountByGeneName(chromosomeReportFromAPI);

			Map<String, Integer> diffMap = new HashMap<>();

			Set<String> geneNamesFromFTP = reportCountByGeneNameFromFTP.keySet();
			Set<String> geneNamesFromAPI = reportCountByGeneNameFromAPI.keySet();

			// check that all gene names from API contained in FTP
			for (String geneNameFromApi : geneNamesFromAPI) {

				if (!geneNamesFromFTP.contains(geneNameFromApi)) {
					throw new IllegalStateException("gene name " + geneNameFromApi + " was not found in ftp chromosome report");
				}
			}

			for (String geneNameFromFTP : geneNamesFromFTP) {

				int diff = reportCountByGeneNameFromFTP.get(geneNameFromFTP) - reportCountByGeneNameFromAPI.getOrDefault(geneNameFromFTP, 0);

				if (diff > 0)
					diffMap.put(geneNameFromFTP, diff);
			}

			differences.setGeneDuplicatesDelta(diffMap);
		}

		private Map<String, Integer> getEntryReportCountByGeneName(ChromosomeReport chromosomeReport) {

			Map<String, Counter> map = new HashMap<>();

			chromosomeReport.getEntryReports().stream()
					.map(EntryReport::getGeneName)
					.filter(gn -> !"-".equals(gn))
					.forEach(acc -> {
						if (!map.containsKey(acc))
							map.put(acc, Counter.newCounter());
						map.get(acc).addAndGet(1);
					});

			return map.entrySet().stream()
					.collect(Collectors.toMap(Map.Entry::getKey, p -> (int)p.getValue().get()));
		}

		Differences getDifferences() {

			return differences;
		}

		public static String reportAllChromosomes() throws IOException, ParseException {

			StringBuilder sb = new StringBuilder();

			sb.append(Differences.getHeaders().stream().collect(Collectors.joining("\t"))).append("\n");
			for (String chromosome : ChromosomeReportService.getChromosomeNames()) {

				DifferenceAnalyser analyser = new DifferenceAnalyser(chromosome);
				sb.append(analyser.getDifferences().getValues().stream().collect(Collectors.joining("\t"))).append("\n");
			}

			return sb.toString();
		}
	}

	private static class Differences {

		private final DifferenceAnalyser analyser;

		private final Set<String> distinctEntryReportAccsInAPI = new HashSet<>();
		private final Set<String> distinctEntryReportAccsInFTP = new HashSet<>();
		private final Set<String> distinctEntryReportGenesInAPI = new HashSet<>();
		private final Set<String> distinctEntryReportGenesInFTP = new HashSet<>();
		private int countReportsInApi;
		private int countReportsInFTP;
		private int countEntryDelta;
		private int countGeneDelta;
		private Map<String, Integer> geneDuplicatesDelta;

        private Differences(DifferenceAnalyser analyser) {
			this.analyser = analyser;
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

		public void setGeneDuplicatesDelta(Map<String, Integer> geneDuplicatesDelta) {
			this.geneDuplicatesDelta = geneDuplicatesDelta;
		}

		public void setCountReportsInApi(int countReportsInApi) {
			this.countReportsInApi = countReportsInApi;
		}

		public void setCountReportsInFTP(int countReportsInFTP) {
			this.countReportsInFTP = countReportsInFTP;
		}

		public void setCountEntryDelta(int countEntryDelta) {
			this.countEntryDelta = countEntryDelta;
		}

		public void setCountGeneDelta(int countGeneDelta) {
			this.countGeneDelta = countGeneDelta;
		}

		public static List<String> getHeaders() {
			return Arrays.asList("chromosome",
					"report count delta (abs(api-ftp))", "report count (api)", "report count (ftp)",
					"entry count delta (abs(api-ftp))", "entry count (api)", "entry count (ftp)",
					"gene count delta (abs(api-ftp))", "gene count (api)", "gene count (ftp)",
					"distinct entry count (api)", "distinct entry count (ftp)", "distinct entry list (ftp)",
					"distinct gene count (api)", "distinct gene count (ftp)", "distinct gene list (ftp)",
                    "gene repeats count (ftp-api)", "gene repeats delta (ftp-api)"
			);
		}

		public List<String> getValues() {

			return Arrays.asList(analyser.chromosome,
					String.valueOf(Math.abs(countReportsInApi - countReportsInFTP)), String.valueOf(countReportsInApi), String.valueOf(countReportsInFTP),
					String.valueOf(countEntryDelta), String.valueOf(analyser.chromosomeReportFromAPI.getSummary().getEntryCount()), String.valueOf(analyser.chromosomeReportFromFTP.getSummary().getEntryCount()),
					String.valueOf(countGeneDelta), String.valueOf(analyser.chromosomeReportFromAPI.getSummary().getGeneCount()), String.valueOf(analyser.chromosomeReportFromFTP.getSummary().getGeneCount()),
					String.valueOf(distinctEntryReportAccsInAPI.size()), String.valueOf(distinctEntryReportAccsInFTP.size()), distinctEntryReportAccsInFTP.toString(),
					String.valueOf(distinctEntryReportGenesInAPI.size()), String.valueOf(distinctEntryReportGenesInFTP.size()), distinctEntryReportGenesInFTP.toString(),
					String.valueOf(geneDuplicatesDelta.values().stream().mapToInt(Integer::intValue).sum()), geneDuplicatesDelta.toString()
			);
		}
	}
}

