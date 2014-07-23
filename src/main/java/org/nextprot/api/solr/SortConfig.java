package org.nextprot.api.solr;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.nextprot.utils.Pair;

public class SortConfig {
	private String name;
	private List<Pair<IndexField, ORDER>> sorting;
	private int boost = -1;
	
	private SortConfig(String name, IndexField field, ORDER order) {
		this.name = name;
		this.sorting = new ArrayList<Pair<IndexField,ORDER>>();
		this.sorting.add(Pair.create(field, order));
	}
	
	private SortConfig(String name, IndexField field, ORDER order, int boost) {
		this(name, field, order);
		this.boost = boost;
	}

	private SortConfig(String name, Pair<IndexField, ORDER>[] sorting) {
		this.name = name;
		this.sorting = new ArrayList<Pair<IndexField,ORDER>>();
		for(Pair<IndexField, ORDER> p : sorting)
			this.sorting.add(p);
	}
	
	private SortConfig(String name, Pair<IndexField, ORDER>[] sorting, int boost) {
		this.name = name;
		this.sorting = new ArrayList<Pair<IndexField,ORDER>>();
		for(Pair<IndexField, ORDER> p : sorting)
			this.sorting.add(p);
		this.boost = boost;
	}
	
	public static SortConfig create(String name, IndexField field, ORDER order) {
		return new SortConfig(name, field, order);
	}
	
	public static SortConfig create(String name, IndexField field, ORDER order, int boost) {
		return new SortConfig(name, field, order, boost);
	}
	
	public static SortConfig create(String name, Pair<IndexField, ORDER>[] sorting) {
		return new SortConfig(name, sorting);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Pair<IndexField, ORDER>> getSorting() {
		return sorting;
	}

	public int getBoost() {
		return boost;
	}
	
	

}
	