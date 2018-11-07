package org.nextprot.api.solr.query.impl.config;

import org.nextprot.api.solr.core.SolrField;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FieldConfigSet {
		
	private IndexParameter parameter;
	private Map<SolrField, Integer> configs;
	
	public FieldConfigSet(IndexParameter parameter) {
		this.parameter = parameter;
		this.configs = new HashMap<>();
	}
	
	public FieldConfigSet add(SolrField field) {
		this.configs.put(field, 0);
		return this;
	}
	
	public FieldConfigSet addWithBoostFactor(SolrField field, int boost) {
		this.configs.put(field, boost);
		return this;
	}

	public IndexParameter getParameter() {
		return parameter;
	}

    public Set<SolrField> getIndexFields() {
        return configs.keySet();
    }

	public int getBoostFactor(SolrField indexField) {

		return configs.get(indexField);
	}
}
