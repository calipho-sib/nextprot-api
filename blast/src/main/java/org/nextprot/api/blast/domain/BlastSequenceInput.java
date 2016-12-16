package org.nextprot.api.blast.domain;

import org.nextprot.api.blast.service.BlastProgram;

public class BlastSequenceInput extends BlastProgram.Params {

    private String header;
    private String sequence;
    private BlastSearchParams blastSearchParams;

    public BlastSequenceInput(String binPath, String nextprotBlastDbPath) {
        super(binPath, nextprotBlastDbPath);
    }

    public String getHeader() {

        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getSequence() {

        return sequence;
    }

    public void setSequence(String sequence) {

        this.sequence = sequence;
    }

    public void setBlastSearchParams(BlastSearchParams blastSearchParams) {
        this.blastSearchParams = blastSearchParams;
    }


    public BlastSearchParams getSearchParams() {

        return blastSearchParams;
    }
}
