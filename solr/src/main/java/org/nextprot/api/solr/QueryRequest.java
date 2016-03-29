package org.nextprot.api.solr;

import org.nextprot.api.commons.utils.KeyValueRepresentation;

import java.io.Serializable;
import java.util.List;

//TODO ? @JsonIgnoreProperties({ "sparqlTitle", "sparqlEngine" })
public class QueryRequest implements Serializable, KeyValueRepresentation {
	private static final long serialVersionUID = 1173041326534229259L;

	private String query;
	private String listOwner;
	private String chromosome = null; //chromosome
	private String listId = null; //list id
	private String queryId = null; //nextprot query id

	public String getListId() {
		return listId;
	}

	public void setListId(String listId) {
		this.listId = listId;
	}

	public String getQueryId() {
		return queryId;
	}

	public void setQueryId(String queryId) {
		this.queryId = queryId;
	}

	private String quality;
	private String sort;
	private String order;
	private String start;
	private String rows;
	private String filter;
	private String sparql;
	private String mode;
	private String sparqlTitle;
	private String sparqlEngine;



	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}


	public boolean hasList() {
		return this.listId  != null;
	}


	public String getListOwner() {
		return this.listOwner;
	}

	public void setListOwner(String owner) {
		this.listOwner = owner;
	}

	public String getQuality() {
		return quality;
	}

	public void setQuality(String quality) {
		this.quality = quality;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getRows() {
		return rows;
	}

	public void setRows(String rows) {
		this.rows = rows;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();

		if (this.query != null) {
			builder.append(this.query);
			builder.append("\n");
		}
		if (this.quality != null) {
			builder.append(this.quality);
			builder.append("\n");
		}
		if (this.start != null) {
			builder.append(this.start);
			builder.append("\n");
		}
		if (this.sort != null) {
			builder.append(this.sort);
			builder.append("\n");
		}
		if (this.order != null) {
			builder.append(this.order);
			builder.append("\n");
		}
		if (this.rows != null) {
			builder.append(this.rows);
			builder.append("\n");
		}
		if (this.filter != null) {
			builder.append(this.filter);
			builder.append("\n");
		}
		if (this.mode != null) {
			builder.append(this.mode);
			builder.append("\n");
		}

		return builder.toString();
	}

	public String toPrettyString() {
		StringBuilder builder = new StringBuilder();
		builder.append("listOwner    : "+this.listOwner+"\n");
		builder.append("list         : "+this.listId+"\n");
		builder.append("query        : "+this.query+"\n");
		builder.append("quality      : "+this.quality+"\n");
		builder.append("start        : "+this.start+"\n");
		builder.append("sort         : "+this.sort+"\n");
		builder.append("order        : "+this.order+"\n");
		builder.append("rows         : "+this.rows+"\n");
		builder.append("filter       : "+this.filter+"\n");
		builder.append("mode         : "+this.mode+"\n");
		builder.append("sparqlTitle  : "+this.sparqlTitle+"\n");
		builder.append("sparqlEngine : "+this.sparqlEngine+"\n");
		builder.append("sparql       : "+this.sparql+"\n");
		builder.append("chromosome   : "+this.chromosome+"\n");
		builder.append("listId       : "+this.listId+"\n");
		builder.append("queryId       : "+this.queryId+"\n");
		builder.append("listId       : "+this.listId+"\n");

		
		return builder.toString();
	}

	public boolean validate() {
		if (this.query != null)
			return false;

		return true;
	}

	public String getSparql() {
		return sparql;
	}

	public void setSparql(String sparql) {
		this.sparql = sparql;
	}

	public boolean hasSparql() {
		return sparql != null;
	}

	public boolean hasNextProtQuery() {
		return queryId != null;
	}

	public String getSparqlTitle() {
		return sparqlTitle;
	}

	public void setSparqlTitle(String sparqlTitle) {
		this.sparqlTitle = sparqlTitle;
	}

	public String getSparqlEngine() {
		return sparqlEngine;
	}

	public void setSparqlEngine(String sparqlEngine) {
		this.sparqlEngine = sparqlEngine;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getChromosome() {
		return chromosome;
	}

	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}
	
	@Override
	public String toKeyValueString() {
		StringBuilder sb = new StringBuilder();
		sb.append(QueryRequest.class.getName() + ";");
		sb.append("mode=" + this.mode + ";");
		if (this.mode != null && this.mode.equals("advanced")) {
			if (this.sparql != null)
				sb.append("sparql=\"" + this.sparql + "\";");
		} else {
			if (this.query != null)
				sb.append("query=" + this.query + ";");
		}
		return sb.toString();
	}

	public boolean hasChromosome() {
		return chromosome != null;
	}

}
