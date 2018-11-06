package org.nextprot.api.solr.query.config;

import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.nextprot.api.commons.utils.Pair;
import org.nextprot.api.solr.core.SolrField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SortConfig {
	private String name;
	private List<Pair<SolrField, ORDER>> sorting;
	private int boost = -1;
	
	private SortConfig(String name, SolrField field, ORDER order) {
		this.name = name;
		this.sorting = new ArrayList<>();
		this.sorting.add(Pair.create(field, order));
	}
	
	private SortConfig(String name, SolrField field, ORDER order, int boost) {
		this(name, field, order);
		this.boost = boost;
	}

	private SortConfig(String name, Pair<SolrField, ORDER>[] sorting) {
		this.name = name;
		this.sorting = new ArrayList<>();
		this.sorting.addAll(Arrays.asList(sorting));
	}
	
	public static SortConfig create(String name, SolrField field, ORDER order) {
		return new SortConfig(name, field, order);
	}
	
	public static SortConfig create(String name, SolrField field, ORDER order, int boost) {
		return new SortConfig(name, field, order, boost);
	}
	
	public static SortConfig create(String name, Pair<SolrField, ORDER>[] sorting) {
		return new SortConfig(name, sorting);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Pair<SolrField, ORDER>> getSorting() {
		return sorting;
	}

	public int getBoost() {
		return boost;
	}
	
	

}
	