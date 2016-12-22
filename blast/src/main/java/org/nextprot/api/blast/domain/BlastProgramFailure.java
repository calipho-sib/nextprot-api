package org.nextprot.api.blast.domain;

import org.nextprot.api.blast.service.BlastProgram;
import org.nextprot.api.commons.utils.ExceptionWithReason;

public class BlastProgramFailure implements BlastProgramOutput {

	private final BlastProgram.Params params;
	private final ExceptionWithReason exceptionWithReason;

	public BlastProgramFailure(BlastProgram.Params params, ExceptionWithReason exceptionWithReason) {

		this.params = params;
		this.exceptionWithReason = exceptionWithReason;
	}

	@Override
	public BlastProgram.Params getQuery() {
		return params;
	}

	public ExceptionWithReason.Reason getError() {

		return exceptionWithReason.getReason();
	}

	public boolean isSuccess() {
		return false;
	}
}
