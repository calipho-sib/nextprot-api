package org.nextprot.api.solr;

import org.apache.solr.client.solrj.SolrQuery.ORDER;


public class Query {

	private String indexName;
	private SolrIndex index;
	private String configuration;
	private String field; // q
	private String queryString; // q => field:value ex. id: NX_...
	private String filter; // fq
	private String sort;
	private ORDER order;
	private int start = 0;
	private int rows;
	
	public Query(SolrIndex index) {
		this(index, null);
	}
	
	public Query(SolrIndex index, String configuration) {
		this.index = index;
		this.indexName = index.getName();
		this.configuration = configuration;
	}
	
	public Query addQuery(String value) {
		this.queryString = value;
		return this;
	}
	
	public Query addQuery(String field, String value) {
		this.field = field;
		this.queryString = value;
		return this;
	}
	
	public Query addQuery(IndexField field, String value) {
		return addQuery(field.getName(), value);
	}
	
	public Query addFilter(String filter) {
		this.filter = filter;
		return this;
	}
	
	public Query sort(String sort) {
		this.sort = sort;
		return this;
	}
	
	public String getSort() {
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

	public SolrIndex getIndex() {
		return index;
	}

	public void setIndex(SolrIndex index) {
		this.index = index;
	}

	public String getConfigName() {
		return configuration;
	}
	
	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}

	public String getField() {
		return field;
	}

	public String getQueryString() {
		return queryString;
	}

	public String getFilter() {
		return filter;
	}

	public Query start(int start) {
		this.start = start;
		return this;
	}
	
	public int getStart() {
		return this.start;
	}

	public int getRows() {
		return rows;
	}

	public Query rows(int rows) {
		this.rows = rows;
		return this;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		String NEWLINE = "\n";
		
		builder.append(indexName);
		builder.append(NEWLINE);
		builder.append(index.getName());
		builder.append(NEWLINE);
		builder.append(configuration);
		builder.append(NEWLINE);
		builder.append(field);
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
	
	public enum SortOrder {
		ASC("asc"),
		DESC("desc");
		
		private String label;
		
		SortOrder(String label) {
			this.label = label;
		}
		
		public String getLabel() {
			return this.label;
		}
	}
}
