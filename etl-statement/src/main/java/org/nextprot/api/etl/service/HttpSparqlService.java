package org.nextprot.api.etl.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface HttpSparqlService {

	SparqlResponse executeSparqlQuery(String sparqlUrl, String query);

	SparqlResponse executeSparqlQuery(String query);

	class SparqlResponse {

		private final List<String> vars = new ArrayList<>();
		private final Map<String, List<String>> results = new HashMap<>();

		public List<String> getVars() {
			return vars;
		}

		public Map<String, List<String>> getResults() {
			return results;
		}

		public void addResult(String key, String value) {

			if (!results.containsKey(key)) {
				vars.add(key);
				results.put(key, new ArrayList<>());
			}

			results.get(key).add(value);
		}

		public int rows() {

			if (results.isEmpty()) {
				return 0;
			}

			return results.get(vars.get(0)).size();
		}
	}
}
