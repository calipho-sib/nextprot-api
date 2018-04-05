package org.nextprot.api.core.domain;

import org.nextprot.api.commons.bio.AminoAcidCode;

import java.io.Serializable;

public class AminoAcid implements Serializable {

	private static final long serialVersionUID = 2L;
	private AminoAcidCode code;
	private int position;
	private int phase;

	public AminoAcid(int position, int phase, AminoAcidCode code) {
		super();
		this.code = code;
		this.position = position;
		this.phase = phase;
	}

	public int getPhase() {
		return phase;
	}

	public void setPhase(int phase) {
		this.phase = phase;
	}

	public AminoAcidCode getCode() {
		return code;
	}

	public void setCode(AminoAcidCode code) {
		this.code = code;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	@Override
	public String toString() {

		return (code != null) ? code.get3LetterCode():"?"+", phase="+phase+", position="+position;
	}
}
