package org.nextprot.api.solr.core.impl.config;

import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.nextprot.api.commons.utils.Pair;
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
	private List<Pair<F, ORDER>> sorting;
	private int boost = -1;
	
	public SortConfig(Criteria criteria, F field, ORDER order) {
		this.criteria = criteria;
		this.sorting = new ArrayList<>();
		this.sorting.add(Pair.create(field, order));
	}

	public SortConfig(Criteria criteria, F field, ORDER order, int boost) {
		this(criteria, field, order);
		this.boost = boost;
	}

	public SortConfig(Criteria criteria, List<Pair<F, ORDER>> sorting) {
		this.criteria = criteria;
		this.sorting = new ArrayList<>();
		this.sorting.addAll(sorting);
	}

	public Criteria getCriteria() {
		return criteria;
	}

	public List<Pair<F, ORDER>> getSorting() {
		return sorting;
	}

	public int getBoost() {
		return boost;
	}
}
	