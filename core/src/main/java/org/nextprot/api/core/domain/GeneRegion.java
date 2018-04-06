package org.nextprot.api.core.domain;

import java.io.Serializable;

public class GeneRegion implements Serializable {

	private static final long serialVersionUID = 2L;

	private String geneName;
	private int firstPosition;
	private int lastPosition;

	public GeneRegion() {}

	public GeneRegion(String geneName, int firstPosition, int lastPosition) {

		this.geneName = geneName;
		this.firstPosition = firstPosition;
		this.lastPosition = lastPosition;
	}

	public int getLastPosition() {
		return lastPosition;
	}

	public void setLastPosition(int lastPosition) {
		this.lastPosition = lastPosition;
	}

	public int getFirstPosition() {
		return firstPosition;
	}

	public void setFirstPosition(int firstPosition) {
		this.firstPosition = firstPosition;
	}

	public String getGeneName() {
		return geneName;
	}

	public void setGeneName(String geneName) {
		this.geneName = geneName;
	}

	public int getLength() {
		return lastPosition - firstPosition + 1;
	}
}
