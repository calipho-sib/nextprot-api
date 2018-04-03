package org.nextprot.api.core.domain.exon;

import org.nextprot.api.core.utils.exon.ExonCategory;

import java.io.Serializable;

public class ExonStart extends CategorizedExon implements Serializable {

	private static final long serialVersionUID = 1L;

	private int startPosition;

	public ExonStart() {

		super(ExonCategory.START);
	}

	public ExonStart(int startPosition) {

		this();
		this.startPosition = startPosition;
	}

	public int getStartPosition() {
		return startPosition;
	}
}
