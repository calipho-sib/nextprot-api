package org.nextprot.api.solr.core.impl.config;


import org.nextprot.api.solr.core.SearchMode;
import org.nextprot.api.solr.query.Query;

public class SearchByIdConfiguration extends IndexConfiguration {

	public SearchByIdConfiguration(SearchMode mode) {
		super(mode);
	}
	
	@Override
	public String formatQuery(Query query) {
		return query.getQueryString(true);
	}
}
