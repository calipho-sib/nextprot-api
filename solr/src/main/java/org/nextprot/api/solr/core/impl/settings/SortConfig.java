package org.nextprot.api.solr.core.impl.settings;

import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.nextprot.api.solr.core.SolrField;

import java.util.ArrayList;
import java.util.List;

public class SortConfig<F extends SolrField> {

	private List<SortBy<F>> sorting;
	private int boost = -1;
	
	public SortConfig(SortBy<F> sortBy) {
		this.sorting = new ArrayList<>();
		this.sorting.add(sortBy);
	}

	public SortConfig(SortBy<F> sortBy, int boost) {
		this(sortBy);
		this.boost = boost;
	}

	public SortConfig(List<SortBy<F>> sorting) {
		this.sorting = new ArrayList<>();
		this.sorting.addAll(sorting);
	}

	public List<SortBy<F>> getSorting() {
		return sorting;
	}

	public int getBoost() {
		return boost;
	}

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
	