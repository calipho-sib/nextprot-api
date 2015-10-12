package org.nextprot.api.core.domain.release;

import java.io.Serializable;

public class ReleaseStatsTag implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String tag, description;
	private int count;
	
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}

}
