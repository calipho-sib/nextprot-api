package org.nextprot.api.core.domain;

import java.io.Serializable;

public class CvDatabase implements Serializable {

	private static final long serialVersionUID = 291460902048921677L;

	private long id;
	private String name;
	private String url;
	private long catId;
	private String catName;
	
	public CvDatabase(long id, String name, String url, long catId, String catName) {
		this.id = id;
		this.name = name;
		this.url = url;
		this.catId= catId;
		this.catName= catName;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	public long getCatId() {
		return catId;
	}

	public String getCatName() {
		return catName;
	}
	
}
