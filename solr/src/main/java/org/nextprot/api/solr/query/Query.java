package org.nextprot.api.solr.query;

import com.google.common.base.Preconditions;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.nextprot.api.solr.core.SolrCore;
import org.nextprot.api.solr.core.SolrField;
import org.nextprot.api.solr.core.impl.settings.SortConfig;


public class Query<F extends SolrField> {

	private String indexName;
	private SolrCore<F> solrCore;
	private QueryMode queryMode;
	private QueryConfiguration<F> queryConfiguration;
	private String queryString; // q => field:value ex. id: NX_...
	private String filter; // fq
	private SortConfig<F> sortConfig; // first sort config then sort and order else default config
	private SortConfig.Criteria sort;
	private ORDER order;
	private int start = 0;
	private int rows;
    
    public Query(SolrCore<F> solrCore) {
		this(solrCore, solrCore.getQuerySettings().getDefaultMode());
	}
	
	public Query(SolrCore<F> solrCore, QueryMode queryMode) {

		Preconditions.checkNotNull(solrCore);

		this.solrCore = solrCore;
		this.indexName = solrCore.getAlias().getName();
		this.queryMode = queryMode;
		this.queryConfiguration = solrCore.getQuerySettings().getConfig(queryMode);
	}


	public Query<F> addQuery(String value) {
		this.queryString=value;
		return this;
	}
	
	public Query<F> addFilter(String filter) {
		this.filter = filter;
		return this;
	}
	
	public Query<F> sort(SortConfig.Criteria sort) {
		this.sort = sort;
		return this;
	}

	public SortConfig<F> getSortConfig() {
		return sortConfig;
	}

	public Query<F> sortConfig(SortConfig<F> sortConfig) {
		this.sortConfig = sortConfig;
        return this;
	}

	public SortConfig.Criteria getSort() {
		return this.sort;
	}
	
	public ORDER getOrder() {
		return order;
	}

	public void order(ORDER order) {
		this.order = order;
	}
	
	public String getIndexName() {
		return this.indexName;
	}
	
	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

	public SolrCore<F> getSolrCore() {
		return solrCore;
	}

	public QueryMode getQueryMode() {
		return queryMode;
	}

	public QueryConfiguration<F> getQueryConfiguration() {
		return queryConfiguration;
	}

	public void setQueryMode(QueryMode queryMode) {
		this.queryMode = queryMode;
	}

	/**
	 * We always want to use the private name of the fields (private name = the one known by solr)
	 * @return
	 */
	public String getQueryString() {
		return getQueryStringWithPrivateFieldNames(false);
	}
	

	public String getQueryStringEscapeColon() {

		return getQueryStringWithPrivateFieldNames(true);
	}

	/**
	 * Escaping non field related colon is mandatory for SolrService.buildSolrIdQuery()
	 * which is called by SolrService.executeIdQuery() called by SearchController.searchIds()
	 * @return
	 */
	private String getQueryStringWithPrivateFieldNames(boolean escapeColon) {
		if(queryString == null) return null;
		
		String qs = this.queryString;

		// remove any backslash
        qs = qs.replace("\\","");        			

        // escape <:> everywhere if requested
        if (escapeColon) {
        	qs = qs.replace(":","\\:");
        }

        // replace public field names with private ones (known by solr)
        for (SolrField f: solrCore.getSchema()) {
        	if (f.hasPublicName()) {
        		String esc = escapeColon ? "\\" : "";
                qs = qs.replace(f.getPublicName() + esc + ":", f.getName() + ":");
        	}
        }

        return qs;
	}

	public String getFilter() {
		return filter;
	}

	public Query<F> start(int start) {
		this.start = start;
		return this;
	}
	
	public int getStart() {
		return this.start;
	}

	public int getRows() {
		return rows;
	}

	public Query<F> rows(int rows) {
		this.rows = rows;
		return this;
	}
	

	public String toPrettyString() {
		StringBuilder builder = new StringBuilder();
		builder.append("indexName       : "+indexName + "\n");
		builder.append("index.getAlias  : "+solrCore.getAlias().getName() + "\n");
		builder.append("configuration   : "+ queryMode + "\n");
		builder.append("queryString     : "+queryString + "\n");
		builder.append("filter          : "+filter + "\n");
		builder.append("sort            : "+sort + "\n");
		if(order != null) {
			builder.append("order   : "+order.name() + "\n");
		}
		builder.append("start           : "+start + "\n");
		builder.append("rows            : "+rows + "\n");
		return builder.toString();
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		String NEWLINE = "\n";
		
		builder.append(indexName);
		builder.append(NEWLINE);
		builder.append(solrCore.getAlias().getName());
		builder.append(NEWLINE);
		builder.append(queryMode);
		builder.append(NEWLINE);
		builder.append(queryString);
		builder.append(NEWLINE);
		builder.append(filter);
		builder.append(NEWLINE);
		builder.append(sort);
		
		if(order != null) {
			builder.append(NEWLINE);
			builder.append(order.name());
		}
		builder.append(NEWLINE);
		builder.append(start);
		builder.append(NEWLINE);
		builder.append(rows);
	
		return builder.toString();
	}
}
