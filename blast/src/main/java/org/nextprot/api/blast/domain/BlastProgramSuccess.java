package org.nextprot.api.blast.domain;

import org.nextprot.api.blast.service.BlastProgram;

import java.io.Serializable;

public class BlastProgramSuccess<R extends Serializable> implements BlastProgramOutput {

	private final BlastProgram.Params params;
	private final R result;

	public BlastProgramSuccess(BlastProgram.Params params, R result) {

		this.params = params;
		this.result = result;
	}

	@Override
	public BlastProgram.Params getQuery() {
		return params;
	}

	public R getData() {

		return result;
	}

	public boolean isSuccess() {

		return true;
	}
}
