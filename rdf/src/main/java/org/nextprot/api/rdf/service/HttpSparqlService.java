package org.nextprot.api.rdf.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface HttpSparqlService {

	SparqlResponse executeSparqlQuery(String sparqlUrl, String query);

	SparqlResponse executeSparqlQuery(String query);

	class SparqlResponse {

		private final Map<String, List<String>> results = new HashMap<>();

		public List<String> getVars() {

			return new ArrayList<>(results.keySet());
		}

		public List<String> getResults(String var) {

			if (!results.containsKey(var)) {
				return new ArrayList<>();
			}
			return results.get(var);
		}

		public <T> List<T> mapResults(String var, Function<String, T> conv) {

			List<T> tList = new ArrayList<>();

			if (!results.containsKey(var)) {
				return tList;
			}

			for (String value : results.get(var)) {

				tList.add(conv.apply(value));
			}

			return tList;
		}

		public void addResult(String key, String value) {

			results.putIfAbsent(key, new ArrayList<>());
			results.get(key).add(value);
		}

		public int rows() {

			if (results.isEmpty()) {
				return 0;
			}

			return results.get(getVars().get(0)).size();
		}

		public static Function<String, String> newRdfEntryConv() {

			return rdfEntry -> (rdfEntry.contains("/")) ? rdfEntry.substring(rdfEntry.lastIndexOf('/') + 1) : rdfEntry;
		}
	}
}
