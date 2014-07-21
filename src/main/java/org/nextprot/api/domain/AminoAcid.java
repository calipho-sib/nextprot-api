package org.nextprot.api.domain;

import java.io.Serializable;

public class AminoAcid implements Serializable{

	private static final long serialVersionUID = 1838981287480263681L;
	private char base;
	private int position;
	private int phase;


	public AminoAcid(int position, int phase, char base) {
		super();
		this.base = base;
		this.position = position;
		this.phase = phase;
	}

	public int getPhase() {
		return phase;
	}

	public void setPhase(int phase) {
		this.phase = phase;
	}

	public char getBase() {
		return base;
	}

	public void setBase(char base) {
		this.base = base;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

}
