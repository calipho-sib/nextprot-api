package org.nextprot.api.solr.core.impl;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.exception.SearchConnectionException;
import org.nextprot.api.solr.core.SolrCore;
import org.nextprot.api.solr.core.impl.schema.EntrySolrField;
import org.nextprot.api.solr.core.impl.settings.SortConfig;
import org.nextprot.api.solr.query.Query;
import org.nextprot.api.solr.query.QueryConfiguration;
import org.nextprot.api.solr.query.QueryExecutor;
import org.nextprot.api.solr.query.dto.SearchResult;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class SolrCoreTest {

	@Test
	public void testSolrOnKant() {

		SolrCvCore cvCore = new SolrCvCore("http://kant:8983/solr");
		SolrCoreHttpClient client = cvCore.newSolrClient();
		Assert.assertEquals("http://kant:8983/solr/npcvs1", client.getURL());
	}

	@Test
	public void testSolrOnCrick() {

		SolrCvCore cvCore = new SolrCvCore("http://crick:8983/solr");
		SolrCoreHttpClient client = cvCore.newSolrClient();
		Assert.assertEquals("http://crick:8983/solr/npcvs1", client.getURL());
	}

	@Test
	public void compareResultsFromCrickAndKantForQueryMSH6() throws QueryConfiguration.MissingSortConfigException {

		SolrCore<EntrySolrField> coreBuild = new SolrGoldOnlyEntryCore("http://kant:8983/solr");
		SolrCore<EntrySolrField> coreAlpha = new SolrGoldOnlyEntryCore("http://uat-web2:8983/solr");

		for (SortConfig.Criteria criteria : SortConfig.Criteria.values()) {

			Query<EntrySolrField> queryBuild = new Query<>(coreBuild).rows(50).addQuery("MSH6").sort(criteria);
			Query<EntrySolrField> queryAlpha = new Query<>(coreAlpha).rows(50).addQuery("MSH6").sort(criteria);

			SearchResult bResult = executeQuery(queryBuild);
			SearchResult aResult = executeQuery(queryAlpha);

			SearchResultDiff srd = SearchResultDiff.compare(bResult, aResult);
			Assert.assertTrue("criteria "+criteria+", diffs: "+srd.toString(), srd.equals);
		}
	}

	@Test
	public void compareResultsFromCrickAndKantForQueryMSH6AllSolrFields() throws QueryConfiguration.MissingSortConfigException {

		Set<EntrySolrField> allEntrySolrFieldSet = new HashSet<>(Arrays.asList(EntrySolrField.values()));
		allEntrySolrFieldSet.remove(EntrySolrField.TEXT);
		allEntrySolrFieldSet.remove(EntrySolrField.SCORE);

		SolrCore<EntrySolrField> coreBuild = new SolrGoldOnlyEntryCore("http://kant:8983/solr", allEntrySolrFieldSet);
		SolrCore<EntrySolrField> coreAlpha = new SolrGoldOnlyEntryCore("http://crick:8983/solr", allEntrySolrFieldSet);

		Query<EntrySolrField> queryBuild = new Query<>(coreBuild).rows(50)
				.addQuery("MSH6")
				.sort(SortConfig.Criteria.AC);

		Query<EntrySolrField> queryAlpha = new Query<>(coreAlpha).rows(50)
				.addQuery("MSH6")
				.sort(SortConfig.Criteria.AC);

		SearchResult bResult = executeQuery(queryBuild);
		SearchResult aResult = executeQuery(queryAlpha);

		SearchResultDiff srd = SearchResultDiff.compare(bResult, aResult);
		Assert.assertTrue("criteria "+SortConfig.Criteria.AC+", diffs: "+srd.toString(), srd.equals);
	}

	private static class SearchResultDiff {

		private FieldDiff<String> entity;
		private FieldDiff<String> index;
		private FieldDiff<Float> score;
		private FieldDiff<Long> found;
		private ListDiff results;
		private boolean equals;

		static SearchResultDiff compare(SearchResult sr1, SearchResult sr2) {

			SearchResultDiff diff = new SearchResultDiff();

			diff.entity = new FieldDiff<>(sr1.getEntity(), sr2.getEntity());
			diff.index = new FieldDiff<>(sr1.getIndex(), sr2.getIndex());
			diff.score = new FieldDiff<>(sr1.getScore(), sr2.getScore());
			diff.found = new FieldDiff<>(sr1.getFound(), sr2.getFound());

			if (diff.found.equals) {
				diff.results = new ListDiff(sr1.getResults(), sr2.getResults());
			}

			diff.equals = diff.entity.equals
					&& diff.index.equals
					&& diff.score.equals
					&& diff.found.equals
					&& diff.results.equals;

			return diff;
		}

		public String toString() {

			StringBuilder sb = new StringBuilder();

			sb.append("equals  : ").append(equals).append("\n");
			sb.append("entity  : ").append(entity.toString()).append("\n");
			sb.append("index   : ").append(index.toString()).append("\n");
			sb.append("score   : ").append(score.toString()).append("\n");
			sb.append("found   : ").append(found.toString()).append("\n");
			sb.append("results : ").append(results.toString()).append("\n");

			return sb.toString();
		}
	}

	private static class FieldDiff<T extends Object> {

		private T o1;
		private T o2;
		private boolean equals;

		FieldDiff(T o1, T o2) {

			this(o1, o2, (a, b) -> Objects.equals(a, b));
		}

		FieldDiff(T o1, T o2, BiFunction<T, T, Boolean> op) {

			this.o1 = o1;
			this.o2 = o2;
			this.equals = op.apply(o1, o2);
		}

		public String toString() {

			StringBuilder sb = new StringBuilder();

			sb.append("equals:").append(equals);

			if (!equals) {

				sb.append(" (diff: ").append(o1).append(" != "+o2+")");
			}

			return sb.toString();
		}
	}

	private static class ListDiff {

		private List<Map<String, Object>> l1;
		private List<Map<String, Object>> l2;
		private Map<Integer, MapDiff> diffs;
		private boolean equals;

		ListDiff(List<Map<String, Object>> l1, List<Map<String, Object>> l2) {

			this.l1 = l1;
			this.l2 = l2;
			this.diffs = diffs();
		}

		private Map<Integer, MapDiff> diffs() {

			Map<Integer, MapDiff> diffs = new TreeMap<>();

			int maxIndex = (l1.size() > l2.size()) ? l1.size() : l2.size();

			for (int i = 0; i < maxIndex; i++) {

				Map<String, Object> map1 = l1.get(i);
				Map<String, Object> map2 = l2.get(i);

				MapDiff mapDiff = new MapDiff(map1, map2);

				if (!mapDiff.equals) {
					diffs.put(i, mapDiff);
				}
			}

			equals = diffs.isEmpty();

			return diffs;
		}

		public String toString() {

			StringBuilder sb = new StringBuilder();

			if (!equals) {
				sb.append("diffs:\n");

				for (Map.Entry<Integer, MapDiff> entry : diffs.entrySet()) {

					sb.append("\t"+entry.getKey()+" -> "+entry.getValue().toString()+"\n");
				}
			}

			return sb.toString();
		}
	}

	private static class MapDiff {

		private Map<String, Object> m1;
		private Map<String, Object> m2;
		private Map<String, FieldDiff> diffs;
		private boolean equals;

		MapDiff(Map<String, Object> m1, Map<String, Object> m2) {

			this.m1 = m1;
			this.m2 = m2;
			this.diffs = diffs();
		}

		private Map<String, FieldDiff> diffs() {

			Map<String, FieldDiff> diffs = new HashMap<>();

			Set<String> keys1 = m1.keySet();
			Set<String> keys2 = m2.keySet();

			Set<String> keys = (keys1.size() > keys2.size()) ? keys1 : keys2;

			for (String key : keys) {

				FieldDiff<Object> diffMap;

				if (m1.get(key) instanceof List) {

					List<String> l1 = ((List<String>) m1.get(key)).stream()
							.sorted()
							.collect(Collectors.toList());
					List<String> l2 = ((List<String>) m2.get(key)).stream()
							.sorted()
							.collect(Collectors.toList());

					diffMap = new FieldDiff<>(l1, l2);
				}
				else if (key.equals(EntrySolrField.PUBLI_COMPUTED_COUNT.getName()) ||
						key.equals(EntrySolrField.PUBLI_CURATED_COUNT.getName()) ||
						key.equals(EntrySolrField.PUBLI_LARGE_SCALE_COUNT.getName())) {

					diffMap = new FieldDiff<>(m1.get(key), m2.get(key), (a, b) -> (int)a >= (int)b);
				}
				else if (key.equals(EntrySolrField.INFORMATIONAL_SCORE.getName())) {

					diffMap = new FieldDiff<>(m1.get(key), m2.get(key), (a, b) -> (float)a >= (float)b);
				}

				else {
					diffMap = new FieldDiff<>(m1.get(key), m2.get(key));
				}

				if (!diffMap.equals) {
					diffs.put(key, diffMap);
				}
			}

			equals = diffs.isEmpty();

			return diffs;
		}

		public String toString() {

			StringBuilder sb = new StringBuilder();

			sb.append("equals:").append(equals);

			if (!equals) {

				for (Map.Entry<String, FieldDiff> entry : diffs.entrySet()) {

					if (!entry.getValue().equals) {
						sb.append(" (diff at ").append(entry.getKey()).append(":").append(entry.getValue().o1).append(" != "+entry.getValue().o2+")");
					}
				}
			}

			return sb.toString();
		}
	}

	private SearchResult executeQuery(Query query) throws QueryConfiguration.MissingSortConfigException {

		SolrCore core = query.getSolrCore();

		QueryExecutor executor = new QueryExecutor(core);

		try {
			return executor.execute(query.getQueryConfiguration().convertQuery(query));
		} catch (SolrServerException e) {
			throw new SearchConnectionException("Could not connect to Solr server. Please contact support or try again later.");
		}
	}
}