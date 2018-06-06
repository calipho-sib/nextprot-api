package org.nextprot.api.core.domain;

import java.io.Serializable;
import java.util.Objects;

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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GeneRegion that = (GeneRegion) o;
		return firstPosition == that.firstPosition &&
				lastPosition == that.lastPosition &&
				Objects.equals(geneName, that.geneName);
	}

	@Override
	public int hashCode() {

		return Objects.hash(geneName, firstPosition, lastPosition);
	}

	@Override
	public String toString() {
		return firstPosition + "-" + lastPosition;
	}
}
