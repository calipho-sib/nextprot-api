package org.nextprot.api.solr.config;

import org.nextprot.api.solr.Query;


public class SearchByIdConfiguration extends IndexConfiguration {

	public SearchByIdConfiguration(String name) {
		super(name);
	}
	
	@Override
	public String buildQuery(Query query) {
		return query.getQueryString();
	}

}
