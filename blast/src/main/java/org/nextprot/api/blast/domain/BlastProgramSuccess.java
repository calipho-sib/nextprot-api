package org.nextprot.api.blast.domain;

import org.nextprot.api.blast.service.BlastProgram;

import java.io.Serializable;

public class BlastProgramSuccess implements BlastProgramOutput {

	private final BlastProgram.Params params;
	private final Serializable result;

	public BlastProgramSuccess(BlastProgram.Params params, Serializable result) {

		this.params = params;
		this.result = result;
	}

	@Override
	public BlastProgram.Params getQuery() {
		return params;
	}

	public Serializable getData() {

		return result;
	}

	public boolean isSuccess() {

		return true;
	}
}
