package org.nextprot.api.solr;



public class SearchByIdConfiguration extends IndexConfiguration {

	public SearchByIdConfiguration(String name) {
		super(name);
	}
	
	@Override
	public String buildQuery(Query query) {
		return query.getQueryString();
	}

}
