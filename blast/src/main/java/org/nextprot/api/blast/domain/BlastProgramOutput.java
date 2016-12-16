package org.nextprot.api.blast.domain;

import org.nextprot.api.blast.service.BlastProgram;

import java.io.Serializable;

public interface BlastProgramOutput extends Serializable {

	BlastProgram.Params getQuery();

	boolean isSuccess();
}
