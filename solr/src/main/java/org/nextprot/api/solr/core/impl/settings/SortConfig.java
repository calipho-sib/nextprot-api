package org.nextprot.api.solr.core.impl.settings;

import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.nextprot.api.solr.core.SolrField;

import java.util.ArrayList;
import java.util.List;

public class SortConfig<F extends SolrField> {

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
	private List<SortBy<F>> sorting;
	private int boost = -1;
	
	public SortConfig(Criteria criteria, SortBy<F> sortBy) {
		this.criteria = criteria;
		this.sorting = new ArrayList<>();
		this.sorting.add(sortBy);
	}

	public SortConfig(Criteria criteria, SortBy<F> sortBy, int boost) {
		this(criteria, sortBy);
		this.boost = boost;
	}

	public SortConfig(Criteria criteria, List<SortBy<F>> sorting) {
		this.criteria = criteria;
		this.sorting = new ArrayList<>();
		this.sorting.addAll(sorting);
	}

	public Criteria getCriteria() {
		return criteria;
	}

	public List<SortBy<F>> getSorting() {
		return sorting;
	}

	public int getBoost() {
		return boost;
	}

	public static class SortBy<F extends SolrField> {

		private final F field;
		private final ORDER order;

		public SortBy(F field, ORDER order) {
			this.field = field;
			this.order = order;
		}

		public F getField() {
			return field;
		}

		public ORDER getOrder() {
			return order;
		}
	}
}
	