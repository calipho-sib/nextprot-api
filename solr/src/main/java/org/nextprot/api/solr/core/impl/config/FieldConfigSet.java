package org.nextprot.api.solr.core.impl.config;

import org.nextprot.api.solr.core.SolrField;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FieldConfigSet<F extends SolrField> {
		
	private final IndexParameter parameter;
	private final Map<F, Integer> configs;
	
	public FieldConfigSet(IndexParameter parameter) {
		this.parameter = parameter;
		this.configs = new HashMap<>();
	}
	
	public FieldConfigSet<F> add(F field) {
		this.configs.put(field, 0);
		return this;
	}
	
	public FieldConfigSet<F> addWithBoostFactor(F field, int boost) {
		this.configs.put(field, boost);
		return this;
	}

	public IndexParameter getParameter() {
		return parameter;
	}

    public Set<F> getIndexFields() {
        return configs.keySet();
    }

	public int getBoostFactor(F indexField) {

		return configs.get(indexField);
	}
}
