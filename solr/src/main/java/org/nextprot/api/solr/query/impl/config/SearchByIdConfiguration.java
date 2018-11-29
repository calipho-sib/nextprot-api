package org.nextprot.api.solr.query.impl.config;


import org.nextprot.api.solr.core.SolrField;
import org.nextprot.api.solr.query.Query;
import org.nextprot.api.solr.query.QueryMode;

public class SearchByIdConfiguration<F extends SolrField> extends IndexConfiguration<F> {

	public SearchByIdConfiguration(QueryMode mode, IndexConfiguration<F> defaultConfiguration) {

		super(mode, defaultConfiguration);
	}
	
	@Override
	public String formatQuery(Query query) {

		return (query != null) ? query.getQueryStringEscapeColon() : "";
	}
}
