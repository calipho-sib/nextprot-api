package org.nextprot.api.core.domain.annotation;

import org.nextprot.api.commons.utils.Pair;

public class LocationRange extends Pair<Integer, Integer> {

	private static final long serialVersionUID = -447379532044228221L;

	LocationRange(Integer begin, Integer end) {
		super(begin, end);
	}

	public Integer getLocationBegin() {
		return this.getFirst();
	}

	public Integer getLocationEnd() {
		return this.getSecond();
	}

}
