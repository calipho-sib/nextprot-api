package org.nextprot.api.core.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.commons.utils.Tree;

public class Terminology extends ArrayList<Tree<CvTerm>> implements Serializable {

	private static final long serialVersionUID = -9023113922977914999L;
	private TerminologyCv terminologyCv = null;

	public TerminologyCv getTerminologyCv() {
		return terminologyCv;
	}

	public void setTerminologyCv(TerminologyCv terminologyCv) {
		this.terminologyCv = terminologyCv;
	}

	public Terminology(List<Tree<CvTerm>> cvTermTrees, TerminologyCv terminologyCv) {
		super(cvTermTrees);
		this.terminologyCv = terminologyCv;
	}

	public Terminology() {
		super(new ArrayList<Tree<CvTerm>>());
	}

	public void addTreeRoot(CvTerm root) {
		this.add(new Tree<CvTerm>(root));
	}

	public int getRootsCount() {
		return this.size();
	}


}