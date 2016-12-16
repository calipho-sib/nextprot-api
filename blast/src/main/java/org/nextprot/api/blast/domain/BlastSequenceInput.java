package org.nextprot.api.blast.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.nextprot.api.blast.service.BlastProgram;

@JsonPropertyOrder({
        "header",
        "sequence",
        "search_param"
})
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

    @JsonProperty("search_param")
    public BlastSearchParams getSearchParams() {

        return blastSearchParams;
    }
}
