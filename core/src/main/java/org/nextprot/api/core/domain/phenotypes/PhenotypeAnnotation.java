package org.nextprot.api.core.domain.phenotypes;

import org.nextprot.api.core.domain.annotation.Annotation;

public class PhenotypeAnnotation extends Annotation{

	private static final long serialVersionUID = 7227020829531972963L;

	private String impact;
	private String effect;

	public String getImpact() {
		return impact;
	}
	public void setImpact(String impact) {
		this.impact = impact;
	}
	public String getEffect() {
		return effect;
	}
	public void setEffect(String effect) {
		this.effect = effect;
	}

}
