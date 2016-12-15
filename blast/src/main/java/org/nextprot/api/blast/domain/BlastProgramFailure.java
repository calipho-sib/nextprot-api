package org.nextprot.api.blast.domain;

import org.nextprot.api.blast.service.BlastProgram;
import org.nextprot.api.commons.utils.ExceptionWithReason;

public class BlastProgramFailure implements BlastProgramOutput {

	private final BlastProgram.Config config;
	private final ExceptionWithReason exceptionWithReason;

	public BlastProgramFailure(BlastProgram.Config config, ExceptionWithReason exceptionWithReason) {

		this.config = config;
		this.exceptionWithReason = exceptionWithReason;
	}

	@Override
	public BlastProgram.Config getQuery() {
		return config;
	}

	public ExceptionWithReason.Reason getError() {

		return exceptionWithReason.getReason();
	}

	public boolean isSuccess() {
		return false;
	}
}
