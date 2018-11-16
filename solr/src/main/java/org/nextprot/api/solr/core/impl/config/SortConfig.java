package org.nextprot.api.solr.core.impl.config;

import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.nextprot.api.commons.utils.Pair;
import org.nextprot.api.solr.core.SolrField;

import java.util.ArrayList;
import java.util.List;

public class SortConfig {

	public enum Criteria {
		SCORE,
		NAME,
		GENE,
		PROTEIN,
		FAMILY,
		LENGTH,
		AC,
		CHROMOSOME
		;

		private final String name;

		Criteria() {
			this.name = name().toLowerCase();
		}

		public String getName() {
			return name;
		}
	}

	private Criteria criteria;
	private List<Pair<SolrField, ORDER>> sorting;
	private int boost = -1;
	
	private SortConfig(Criteria criteria, SolrField field, ORDER order) {
		this.criteria = criteria;
		this.sorting = new ArrayList<>();
		this.sorting.add(Pair.create(field, order));
	}
	
	private SortConfig(Criteria criteria, SolrField field, ORDER order, int boost) {
		this(criteria, field, order);
		this.boost = boost;
	}

	private SortConfig(Criteria criteria, List<Pair<SolrField, ORDER>> sorting) {
		this.criteria = criteria;
		this.sorting = new ArrayList<>();
		this.sorting.addAll(sorting);
	}
	
	public static SortConfig create(Criteria criteria, SolrField field, ORDER order) {
		return new SortConfig(criteria, field, order);
	}
	
	public static SortConfig create(Criteria criteria, SolrField field, ORDER order, int boost) {
		return new SortConfig(criteria, field, order, boost);
	}
	
	public static SortConfig create(Criteria criteria, List<Pair<SolrField, ORDER>> sorting) {
		return new SortConfig(criteria, sorting);
	}

	public Criteria getCriteria() {
		return criteria;
	}

	public List<Pair<SolrField, ORDER>> getSorting() {
		return sorting;
	}

	public int getBoost() {
		return boost;
	}
}
	