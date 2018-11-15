package org.nextprot.api.solr.query.impl.config;


import org.nextprot.api.solr.query.Query;

public class SearchByIdConfiguration extends IndexConfiguration {

	public SearchByIdConfiguration(Mode mode) {
		super(mode);
	}
	
	@Override
	public String formatQuery(Query query) {
		return query.getQueryString(true);
	}
}
