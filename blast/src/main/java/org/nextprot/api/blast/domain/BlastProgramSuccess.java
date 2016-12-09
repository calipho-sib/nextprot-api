package org.nextprot.api.blast.domain;

import org.nextprot.api.blast.service.BlastProgram;

import java.io.Serializable;

public class BlastProgramSuccess<R extends Serializable> implements BlastProgramOutput {

	private final BlastProgram.Config config;
	private final R result;

	public BlastProgramSuccess(BlastProgram.Config config, R result) {

		this.config = config;
		this.result = result;
	}

	@Override
	public BlastProgram.Config getConfig() {
		return config;
	}

	public R getResult() {

		return result;
	}

	public boolean isSuccess() {

		return true;
	}
}
