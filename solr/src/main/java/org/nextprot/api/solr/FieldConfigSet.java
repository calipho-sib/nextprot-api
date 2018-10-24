package org.nextprot.api.solr;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FieldConfigSet {
		
	private IndexParameter parameter;
	private Map<IndexField, Integer> configs;
	
	public FieldConfigSet(IndexParameter parameter) {
		this.parameter = parameter;
		this.configs = new HashMap<>();
	}
	
	public FieldConfigSet add(IndexField field) {
		this.configs.put(field, 0);
		return this;
	}
	
	public FieldConfigSet addWithBoostFactor(IndexField field, int boost) {
		this.configs.put(field, boost);
		return this;
	}

	public IndexParameter getParameter() {
		return parameter;
	}

    public Set<IndexField> getIndexFields() {
        return configs.keySet();
    }

	public int getBoostFactor(IndexField indexField) {

		return configs.get(indexField);
	}
}
