package org.nextprot.api.solr.core.impl.config;


import org.nextprot.api.solr.core.SearchMode;
import org.nextprot.api.solr.core.SolrField;
import org.nextprot.api.solr.query.Query;

public class SearchByIdConfiguration<F extends SolrField> extends IndexConfiguration<F> {

	public SearchByIdConfiguration(SearchMode mode) {
		super(mode);
	}
	
	@Override
	public String formatQuery(Query query) {
		return query.getQueryStringEscapeColon();
	}
}
