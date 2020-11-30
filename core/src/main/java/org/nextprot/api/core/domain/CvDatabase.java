package org.nextprot.api.core.domain;

import java.io.Serializable;

public class CvDatabase implements Serializable {

	private static final long serialVersionUID = -8926932264226208184L;

	private long id;
	private String name;
	private long catId;
	private String catName;
	
	public CvDatabase(long id, String name, long catId, String catName) {
		this.id = id;
		this.name = name;
		this.catId= catId;
		this.catName= catName;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public long getCatId() {
		return catId;
	}

	public String getCatName() {
		return catName;
	}
	
}
