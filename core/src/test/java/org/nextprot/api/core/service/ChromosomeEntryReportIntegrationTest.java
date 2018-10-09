package org.nextprot.api.core.service;

import com.google.common.collect.Sets;
import org.apache.lucene.util.Counter;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.commons.bio.Chromosome;
import org.nextprot.api.core.domain.ChromosomeReport;
import org.nextprot.api.core.domain.EntryReport;
import org.nextprot.api.core.service.export.io.ChromosomeReportTXTReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.text.ParseException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.nextprot.api.core.domain.EntryReport.*;
import static org.nextprot.api.core.domain.EntryReportStats.*;


public class ChromosomeEntryReportIntegrationTest {

	@Ignore
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
			try {
				chromosomeReportFromFTP = reader.read(new InputStreamReader(new URL(ftp).openStream()));
				chromosomeReportFromAPI = reader.read(new InputStreamReader(new URL(api).openStream()));
			} catch (ParseException e) {
				throw new ParseException("Error while reading chromosome "+chromosome+": "+e.getMessage(), e.getErrorOffset());
			}
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

			// 7. Test row order
			//compareEntryReportOrders();

			// 8. Compare all entry report values
			compareEntryReports(allProperties());

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
			differences.setCountGeneDelta(Math.abs(summaryAPI.getEntryReportCount() - summaryFTP.getEntryReportCount()));
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

		private void compareEntryReportOrders() {

			List<EntryReport> entryReportsFromAPI = chromosomeReportFromAPI.getEntryReports();
			List<EntryReport> entryReportsFromFTP = chromosomeReportFromFTP.getEntryReports();

			Map<String, Function<EntryReport, String>> funcs = buildGetterFunctionMap();

			for (int i=0 ; i<entryReportsFromAPI.size() ; i++) {

				Differences.EntryReportValueDifferences valueDifferences =
						new Differences.EntryReportValueDifferences(chromosome, entryReportsFromAPI.get(i).getGeneName(),
								entryReportsFromAPI.get(i).getAccession(), i);

				for (String propName : Arrays.asList(GENE_NAME, CODING_STRAND, CHROMOSOMAL_LOCATION,
						GENE_START_POSITION, GENE_END_POSITION)) {

					valueDifferences.checkDifference(propName,
							funcs.get(propName).apply(entryReportsFromAPI.get(i)),
							funcs.get(propName).apply(entryReportsFromFTP.get(i))
					);
				}

				differences.addValueDifferences(valueDifferences);
			}
		}

		private void compareEntryReports(Collection<String> propertiesToCheck) {

			Map<String, EntryReport> entryReportsFromAPI = toMap(chromosomeReportFromAPI.getEntryReports());
			Map<String, EntryReport> entryReportsFromFTP = toMap(chromosomeReportFromFTP.getEntryReports());

			Map<String, Function<EntryReport, String>> funcs = buildGetterFunctionMap();

			if (!Sets.difference(entryReportsFromAPI.keySet(), entryReportsFromFTP.keySet()).isEmpty()) {

				Set<String> apiMinusFtp = Sets.difference(entryReportsFromAPI.keySet(), entryReportsFromFTP.keySet()).immutableCopy();
				Set<String> ftpMinusApi = Sets.difference(entryReportsFromFTP.keySet(), entryReportsFromAPI.keySet()).immutableCopy();

				System.err.println("api - ftp differences: "+apiMinusFtp.size());
				System.err.println("ftp - api differences: "+ftpMinusApi.size());
			}

			for (String key : entryReportsFromFTP.keySet()) {

				Differences.EntryReportValueDifferences valueDifferences =
						new Differences.EntryReportValueDifferences(chromosome, key);

				for (String propName : propertiesToCheck) {

					EntryReport erAPI = entryReportsFromAPI.get(key);
					EntryReport erFTP = entryReportsFromFTP.get(key);

					if (erAPI != null && erFTP != null) {
						valueDifferences.checkDifference(propName,
								funcs.get(propName).apply(erAPI),
								funcs.get(propName).apply(erFTP)
						);
					}
					else {
						System.err.println("key:"+key+", API defined:"+(erAPI != null)+", FTP defined:"+(erFTP != null));
					}
				}

				differences.addValueDifferences(valueDifferences);
			}

		}

		private static Map<String, EntryReport> toMap(List<EntryReport> entryReportList) {

			Map<String, EntryReport> map = new HashMap<>();

			for (EntryReport er : entryReportList) {

				String key = Stream.of(er.getGeneName(), er.getAccession(), er.getChromosomalLocation(), er.getGeneStartPosition(), er.getGeneEndPosition())
						.collect(Collectors.joining("~"));

				if (map.containsKey(key)) {
					System.err.println("key "+key + " already exist");
				}
				else {
					map.put(key, er);
				}
			}
			return map;
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
			for (String chromosome : Chromosome.getNames()) {

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
		private List<EntryReportValueDifferences> entryReportValueDifferenceList = new ArrayList<>();

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
                    "gene repeats count (ftp-api)", "gene repeats delta (ftp-api)",
					"count entry reports diffs", "diffs list"
			);
		}

		public void addValueDifferences(EntryReportValueDifferences valueDifferences) {

        	if (valueDifferences.countDifferences() > 0)
				this.entryReportValueDifferenceList.add(valueDifferences);
		}

		public List<String> getValues() {

			return Arrays.asList(analyser.chromosome,
					String.valueOf(Math.abs(countReportsInApi - countReportsInFTP)), String.valueOf(countReportsInApi), String.valueOf(countReportsInFTP),
					String.valueOf(countEntryDelta), String.valueOf(analyser.chromosomeReportFromAPI.getSummary().getEntryCount()), String.valueOf(analyser.chromosomeReportFromFTP.getSummary().getEntryCount()),
					String.valueOf(countGeneDelta), String.valueOf(analyser.chromosomeReportFromAPI.getSummary().getEntryReportCount()), String.valueOf(analyser.chromosomeReportFromFTP.getSummary().getEntryReportCount()),
					String.valueOf(distinctEntryReportAccsInAPI.size()), String.valueOf(distinctEntryReportAccsInFTP.size()), distinctEntryReportAccsInFTP.toString(),
					String.valueOf(distinctEntryReportGenesInAPI.size()), String.valueOf(distinctEntryReportGenesInFTP.size()), distinctEntryReportGenesInFTP.toString(),
					String.valueOf(geneDuplicatesDelta.values().stream().mapToInt(Integer::intValue).sum()), geneDuplicatesDelta.toString(),
					String.valueOf(entryReportValueDifferenceList.size()), String.valueOf(entryReportValueDifferenceList)
			);
		}

		public static class EntryReportValueDifferences {

        	private final String chromosome;
        	private final String entryReportkey;
			private final Map<String, String> differentValues = new HashMap<>();

			public EntryReportValueDifferences(String chromosome, String geneName, String accession, int entryReportIndex) {

				this.chromosome = chromosome;
				this.entryReportkey = geneName+"."+accession+"."+entryReportIndex;
			}

			public EntryReportValueDifferences(String chromosome, String key) {

				this.chromosome = chromosome;
				this.entryReportkey = key;
			}

			public void checkDifference(String property, String apiValue, String ftpValue) {

				if (property.equals(EntryReport.CHROMOSOMAL_LOCATION) && ftpValue.equals("unknown")) {
					ftpValue = chromosome;
				}

				if (!ftpValue.equals(apiValue))
					differentValues.put(property, "api:"+apiValue+", ftp:"+ftpValue);
			}

			public int countDifferences() {
				return differentValues.size();
			}

			private static String formatMapInJson(Map<String, String> map) {

				StringBuilder sb = new StringBuilder("{");

				sb.append(map.entrySet().stream()
						.map(EntryReportValueDifferences::formatEntryInJson)
						.collect(Collectors.joining(",")));

				sb.append("}");

				return sb.toString();
			}

			private static String formatEntryInJson(Map.Entry<String, String> entry) {

				StringBuilder sb = new StringBuilder();

				sb.append("\"").append(entry.getKey()).append("\"");
				sb.append(":");
				sb.append("\"").append(entry.getValue()).append("\"");

				return sb.toString();
			}

			@Override
			public String toString() {
				return "{" +
						"\"entryReportkey\":\"" + entryReportkey +
						"\", \"count\":" + countDifferences() +
						", \"differences\":" + formatMapInJson(differentValues) +
						'}';
			}
		}
	}

	private static Map<String, Function<EntryReport, String>> buildGetterFunctionMap() {

		Map<String, Function<EntryReport, String>> map = new HashMap<>();

		map.put(GENE_NAME, EntryReport::getGeneName);
		map.put(CODING_STRAND, EntryReport::getCodingStrand);
		map.put(CHROMOSOMAL_LOCATION, EntryReport::getChromosomalLocation);
		map.put(GENE_START_POSITION, EntryReport::getGeneStartPosition);
		map.put(GENE_END_POSITION, EntryReport::getGeneEndPosition);
		map.put(ENTRY_ACCESSION, EntryReport::getAccession);
		map.put(ENTRY_DESCRIPTION, EntryReport::getDescription);
		map.put(PROTEIN_EXISTENCE, EntryReport::getProteinExistence);
		map.put(IS_PROTEOMICS, er -> String.valueOf(er.isProteomics()));
		map.put(IS_ANTIBODY, er -> String.valueOf(er.isAntibody()));
		map.put(IS_DISEASE, er -> String.valueOf(er.isDisease()));
		map.put(IS_3D, er -> String.valueOf(er.is3D()));
		map.put(ISOFORM_COUNT, er -> String.valueOf(er.countIsoforms()));
		map.put(VARIANT_COUNT, er -> String.valueOf(er.countVariants()));
		map.put(PTM_COUNT, er -> String.valueOf(er.countPTMs()));

		return map;
	}

	private static Collection<String> allProperties() {

		return Arrays.asList(GENE_NAME, CODING_STRAND, CHROMOSOMAL_LOCATION, GENE_START_POSITION, GENE_END_POSITION,
				ENTRY_ACCESSION, ENTRY_DESCRIPTION, PROTEIN_EXISTENCE, IS_PROTEOMICS, IS_ANTIBODY, IS_DISEASE,
				IS_3D, ISOFORM_COUNT, VARIANT_COUNT, PTM_COUNT
		);
	}
}

