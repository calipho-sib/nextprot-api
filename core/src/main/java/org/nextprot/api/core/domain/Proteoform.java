package org.nextprot.api.core.domain;

import java.util.List;

/**
 * 
 * @author pmichel
 *
 */
public class Proteoform {

	private String id;
	private String parentIsoformName;
	private String label;
	private List<String> components;

	public Proteoform(String parentIsoformName, String label, List<String> components) {
		this.parentIsoformName = parentIsoformName;
		this.label = label;
		this.components = components;
		buildId();
	}

	public String getId() {
		return id;
	}

	public String getParentIsoformName() {
		return parentIsoformName;
	}

	public String getLabel() {
		return label;
	}

	public List<String> getComponents() {
		return components;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Proteoform other = (Proteoform) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	private void buildId() {
		StringBuilder sb = new StringBuilder(parentIsoformName);
		for (int i = 0; i < components.size(); i++) {
			sb.append("_");
			sb.append(components.get(i));
		}
		this.id = sb.toString();
	}

}
